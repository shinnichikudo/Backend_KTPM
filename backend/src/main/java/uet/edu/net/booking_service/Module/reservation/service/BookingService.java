package uet.edu.net.booking_service.module.reservation.service;

import lombok.RequiredArgsConstructor;
import uet.edu.net.booking_service.module.auth.contract.AuthServiceContract;
import uet.edu.net.booking_service.module.inventory.contract.RoomDTO;
import uet.edu.net.booking_service.module.inventory.contract.RoomServiceContract;
import uet.edu.net.booking_service.module.reservation.contract.BookingServiceContract;
import uet.edu.net.booking_service.module.reservation.domain.BookingPolicy;
import uet.edu.net.booking_service.module.reservation.entity.Booking;
import uet.edu.net.booking_service.module.reservation.entity.BookingDetail;
import uet.edu.net.booking_service.module.reservation.entity.Payment;
import uet.edu.net.booking_service.module.reservation.repository.BookingDetailRepository;
import uet.edu.net.booking_service.module.reservation.repository.BookingRepository;
import uet.edu.net.booking_service.module.reservation.service.PaymentService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class BookingService implements BookingServiceContract {

    // ── Dependencies ────────────────────────────────────────────────────────────
    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;

    private final AuthServiceContract authService;
    private final RoomServiceContract roomService;
    private final TransactionTemplate transactionTemplate;

    private final PaymentService paymentService;

    private final BookingPolicy bookingPolicy = new BookingPolicy();

    // Implement BookingServiceContract
    @Override
    public List<Long> getBookedRoomIds(LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);
        return bookingRepository.findBookedRoomIds(checkIn, checkOut);
    }

    public Booking createBooking(Long userId, Long roomId,
                                 LocalDate checkIn, LocalDate checkOut,
                                 int totalGuest) {

        validateDates(checkIn, checkOut);
        authService.getUserById(userId);

        return transactionTemplate.execute(status -> {
            RoomDTO room = roomService.getRoomForUpdate(roomId);

            if (room.getCapacity() < totalGuest) {
                throw new IllegalArgumentException(
                        "Phòng " + room.getRoomNumber() + " chỉ chứa tối đa "
                                + room.getCapacity() + " khách.");
            }

            boolean isUnavailable = bookingRepository
                    .findBookedRoomIds(checkIn, checkOut)
                    .contains(roomId);
            if (isUnavailable) {
                throw new IllegalStateException(
                        "Phòng " + room.getRoomNumber()
                                + " đã được đặt trong khoảng thời gian này.");
            }

            long nights = bookingPolicy.caculateValidDay(checkIn, checkOut);
            BigDecimal total = bookingPolicy.caculateTotalPrice(room.getBasePrice(), nights);

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
                    .roomId(roomId)
                    .priceAtBooking(room.getBasePrice())
                    .build();
            bookingDetailRepository.save(detail);

            booking.getBookingDetails().add(detail);

            return booking;
        });
    }


    public Payment payBooking(Long bookingId, String cardNumber, String paymentMethod) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy đơn đặt phòng với ID: " + bookingId));


        if (!"PENDING".equals(booking.getStatus())) {
            throw new IllegalStateException(
                    "Đơn đặt phòng #" + bookingId + " không ở trạng thái PENDING.");
        }

        Payment payment = paymentService.processPayment(
                bookingId,
                booking.getTotalPrice(),
                cardNumber,
                paymentMethod
        );


        transactionTemplate.execute(status -> {
            booking.setStatus("PAID");
            bookingRepository.save(booking);
            return null;
        });

        return payment;
    }

    public Booking cancelBooking(Long bookingId, Long userId) {
        return transactionTemplate.execute(status -> {

            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Không tìm thấy đơn đặt phòng với ID: " + bookingId));


            if (!booking.getUserId().equals(userId)) {
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
            return bookingRepository.save(booking);
        });
    }

    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy đơn đặt phòng với ID: " + bookingId));
    }

    public List<Booking> getBookingsByUser(Long userId) {
        authService.getUserById(userId);
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
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