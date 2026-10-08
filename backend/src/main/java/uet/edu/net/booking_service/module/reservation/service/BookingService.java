package uet.edu.net.booking_service.module.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import uet.edu.net.booking_service.core.exception.AppException;
import uet.edu.net.booking_service.core.exception.ErrorCode;
import uet.edu.net.booking_service.module.auth.contract.AuthServiceContract;
import uet.edu.net.booking_service.module.auth.contract.UserDTO;
import uet.edu.net.booking_service.module.inventory.contract.RoomDTO;
import uet.edu.net.booking_service.module.inventory.contract.RoomServiceContract;
import uet.edu.net.booking_service.module.reservation.contract.BookingServiceContract;
import uet.edu.net.booking_service.module.reservation.domain.BookingPolicy;
import uet.edu.net.booking_service.module.reservation.entity.Booking;
import uet.edu.net.booking_service.module.reservation.entity.BookingDetail;
import uet.edu.net.booking_service.module.reservation.entity.BookingNight;
import uet.edu.net.booking_service.module.reservation.entity.Payment;
import uet.edu.net.booking_service.module.reservation.repository.BookingDetailRepository;
import uet.edu.net.booking_service.module.reservation.repository.BookingNightRepository;
import uet.edu.net.booking_service.module.reservation.repository.BookingRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService implements BookingServiceContract {

    // ── Dependencies ────────────────────────────────────────────────────────────
    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final BookingNightRepository bookingNightRepository;

    private final AuthServiceContract authService;
    private final RoomServiceContract roomService;
    private final TransactionTemplate transactionTemplate;

    private final PaymentService paymentService;

    private final BookingPolicy bookingPolicy = new BookingPolicy();

    // Implement BookingServiceContract
    @Override
    public List<Long> getBookedRoomIds(LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);
        return bookingNightRepository.findBookedRoomIds(checkIn, checkOut);
    }

    /** Creates a booking after the authenticated user has been resolved. */
    private Booking createBookingInternal(Long userId, Long roomId,
            LocalDate checkIn, LocalDate checkOut,
            int totalGuest) {

        validateDates(checkIn, checkOut);
        try {
            return transactionTemplate.execute(status -> {
                RoomDTO room = roomService.getActiveRoomById(roomId);

                if (room.capacity() < totalGuest) {
                    throw new IllegalArgumentException(
                            "Phòng " + room.roomNumber() + " chỉ chứa tối đa "
                                    + room.capacity() + " khách.");
                }

                boolean isUnavailable = bookingNightRepository
                        .findBookedRoomIds(checkIn, checkOut)
                        .contains(roomId);
                if (isUnavailable) {
                    throw new AppException(ErrorCode.ROOM_NOT_AVAILABLE);
                }

                long nights = bookingPolicy.caculateValidDay(checkIn, checkOut);
                BigDecimal total = bookingPolicy.caculateTotalPrice(room.basePrice(), nights);

                Booking booking = Booking.builder()
                        .userId(userId)
                        .checkInDate(checkIn)
                        .checkOutDate(checkOut)
                        .totalGuest(totalGuest)
                        .totalPrice(total)
                        .status("PENDING")
                        .build();
                booking = bookingRepository.save(booking);

                BookingDetail detail = BookingDetail.builder()
                        .bookingId(booking.getId())
                        .booking(booking)
                        .roomId(roomId)
                        .priceAtBooking(room.basePrice())
                        .build();
                bookingDetailRepository.save(detail);

                booking.getBookingDetails().add(detail);

                // Lưu từng đêm vào bảng booking_nights (Có Unique Constraint (room_id, night_date) chống double-booking tuyệt đối)
                List<BookingNight> bookingNights = new ArrayList<>();
                for (LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1)) {
                    bookingNights.add(BookingNight.builder()
                            .bookingId(booking.getId())
                            .roomId(roomId)
                            .nightDate(date)
                            .build());
                }
                bookingNightRepository.saveAll(bookingNights);

                return booking;
            });
        } catch (DataIntegrityViolationException ex) {
            // Khi có 2 request đồng thời cùng đặt phòng, Unique Constraint (room_id, night_date) ở DB ném lỗi này
            throw new AppException(ErrorCode.ROOM_NOT_AVAILABLE);
        }
    }

    /** Pays a pending booking only for its owner. */
    @Transactional
    public Payment payBooking(Long bookingId, String cardNumber, String paymentMethod,
            String requesterEmail) {

        Booking booking = findBookingById(bookingId);

        assertOwner(booking, requesterEmail);

        if ("PAID".equals(booking.getStatus())) {
            throw new AppException(ErrorCode.PAYMENT_ALREADY_EXISTS);
        }

        if (!"PENDING".equals(booking.getStatus())) {
            throw new IllegalStateException(
                    "Đơn đặt phòng #" + bookingId + " không ở trạng thái PENDING.");
        }

        Payment payment = paymentService.processPayment(
                bookingId,
                booking.getTotalPrice(),
                cardNumber,
                paymentMethod);

        booking.setStatus("PAID");
        bookingRepository.save(booking);

        return payment;
    }

    /** Cancels a booking for its owner or an ADMIN. */
    public Booking cancelBooking(Long bookingId, String requesterEmail, boolean admin) {
        return transactionTemplate.execute(status -> {

            Long userId = admin ? null : authService.getUserByEmail(requesterEmail).id();

            Booking booking = findBookingById(bookingId);

            if (!admin && !booking.getUserId().equals(userId)) {
                throw new SecurityException(
                        "Người dùng #" + userId
                                + " không có quyền huỷ đơn đặt phòng #" + bookingId + ".");
            }

            if (!bookingPolicy.canCancel(booking.getStatus())) {
                throw new IllegalStateException(
                        "Không thể huỷ đơn #" + bookingId
                                + " vì trạng thái hiện tại là: " + booking.getStatus() + ".");
            }

            booking.setStatus("CANCELLED");
            bookingNightRepository.deleteByBookingId(bookingId); // Giải phóng các đêm phòng đã đặt
            return bookingRepository.save(booking);
        });
    }

    /** Loads a booking without performing authorization. */
    private Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    /** Returns booking details only to its owner or an ADMIN. */
    public Booking getBookingById(Long bookingId, String requesterEmail, boolean admin) {
        Booking booking = findBookingById(bookingId);
        assertOwnerOrAdmin(booking, requesterEmail, admin);
        return booking;
    }

    /** Resolves the authenticated user once, then delegates to the internal create flow. */
    public Booking createBookingByEmail(String email, Long roomId,
            LocalDate checkIn, LocalDate checkOut,
            int totalGuest) {
        uet.edu.net.booking_service.module.auth.contract.UserDTO user = authService.getUserByEmail(email);
        return createBookingInternal(user.id(), roomId, checkIn, checkOut, totalGuest);
    }

    @Transactional(readOnly = true)
    /** Lists bookings for a user ID; the endpoint is restricted to ADMIN. */
    public List<Booking> getBookingsByUser(Long userId) {
        authService.getUserById(userId);
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    /** Lists the authenticated user's own booking history. */
    public List<Booking> getBookingsByUserEmail(String email) {
        uet.edu.net.booking_service.module.auth.contract.UserDTO user = authService.getUserByEmail(email);
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(user.id());
    }

    /** Enforces ownership for customers while allowing ADMIN access. */
    private void assertOwnerOrAdmin(Booking booking, String requesterEmail, boolean admin) {
        if (admin) {
            return;
        }
        assertOwner(booking, requesterEmail);
    }

    /** Enforces ownership for operations that must only be performed by the booking owner. */
    private void assertOwner(Booking booking, String requesterEmail) {
        UserDTO requester = authService.getUserByEmail(requesterEmail);
        if (!booking.getUserId().equals(requester.id())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Ngày nhận phòng và ngày trả phòng không được để trống.");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException(
                    "Ngày nhận phòng phải trước ngày trả phòng.");
        }
    }
}
