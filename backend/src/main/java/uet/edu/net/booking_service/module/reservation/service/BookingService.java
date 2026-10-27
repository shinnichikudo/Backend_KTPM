package uet.edu.net.booking_service.module.reservation.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uet.edu.net.booking_service.core.exception.AppException;
import uet.edu.net.booking_service.core.exception.ErrorCode;
import uet.edu.net.booking_service.module.auth.contract.AuthServiceContract;
import uet.edu.net.booking_service.module.auth.contract.UserDTO;
import uet.edu.net.booking_service.module.inventory.contract.RoomDTO;
import uet.edu.net.booking_service.module.inventory.contract.RoomServiceContract;
import uet.edu.net.booking_service.module.reservation.domain.Booking;
import uet.edu.net.booking_service.module.reservation.domain.BookingPolicy;
import uet.edu.net.booking_service.module.reservation.domain.Payment;
import uet.edu.net.booking_service.module.reservation.domain.RoomNightConflictException;
import uet.edu.net.booking_service.module.reservation.service.port.BookingNightStore;
import uet.edu.net.booking_service.module.reservation.service.port.BookingStore;
import uet.edu.net.booking_service.module.reservation.service.port.BookingUseCases;

@Service
@Transactional
public class BookingService implements BookingUseCases {

    private final BookingStore bookingStore;
    private final BookingNightStore bookingNightStore;
    private final AuthServiceContract authService;
    private final RoomServiceContract roomService;
    private final PaymentService paymentService;
    private final BookingPolicy bookingPolicy = new BookingPolicy();

    public BookingService(BookingStore bookingStore, BookingNightStore bookingNightStore,
            AuthServiceContract authService, RoomServiceContract roomService, PaymentService paymentService) {
        this.bookingStore = bookingStore;
        this.bookingNightStore = bookingNightStore;
        this.authService = authService;
        this.roomService = roomService;
        this.paymentService = paymentService;
    }

    @Override
    public Booking createBookingByEmail(String email, Long roomId, LocalDate checkIn,
            LocalDate checkOut, int totalGuest) {
        UserDTO user = authService.getUserByEmail(email);
        validateDates(checkIn, checkOut);

        RoomDTO room = roomService.getActiveRoomById(roomId);
        if (room.capacity() < totalGuest) {
            throw new IllegalArgumentException(
                    "Phòng " + room.roomNumber() + " chỉ chứa tối đa " + room.capacity() + " khách.");
        }
        if (bookingNightStore.hasReservation(roomId, checkIn, checkOut)) {
            throw new AppException(ErrorCode.ROOM_NOT_AVAILABLE);
        }

        long nights = bookingPolicy.caculateValidDay(checkIn, checkOut);
        BigDecimal total = bookingPolicy.caculateTotalPrice(room.basePrice(), nights);
        Booking booking = new Booking(null, user.id(), checkIn, checkOut, totalGuest,
                total, "PENDING", null, roomId, room.basePrice());

        try {
            booking = bookingStore.save(booking);
            bookingNightStore.reserve(booking.id(), roomId, checkIn, checkOut);
            return booking;
        } catch (RoomNightConflictException exception) {
            throw new AppException(ErrorCode.ROOM_NOT_AVAILABLE);
        }
    }

    @Override
    public Payment payBooking(Long bookingId, String cardNumber, String paymentMethod, String requesterEmail) {
        Booking booking = findBookingById(bookingId);
        assertOwner(booking, requesterEmail);

        if ("PAID".equals(booking.status())) {
            throw new AppException(ErrorCode.PAYMENT_ALREADY_EXISTS);
        }
        if (!"PENDING".equals(booking.status())) {
            throw new IllegalStateException("Đơn đặt phòng #" + bookingId + " không ở trạng thái PENDING.");
        }

        Payment payment = paymentService.processPayment(
                bookingId, booking.totalPrice(), cardNumber, paymentMethod);
        bookingStore.save(booking.withStatus("PAID"));
        return payment;
    }

    @Override
    public Booking cancelBooking(Long bookingId, String requesterEmail, boolean admin) {
        Long userId = admin ? null : authService.getUserByEmail(requesterEmail).id();
        Booking booking = findBookingById(bookingId);

        if (!admin && !booking.userId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        if (!bookingPolicy.canCancel(booking.status())) {
            throw new IllegalStateException(
                    "Không thể hủy đơn #" + bookingId + " vì trạng thái hiện tại là: " + booking.status() + ".");
        }

        bookingNightStore.release(bookingId);
        return bookingStore.save(booking.withStatus("CANCELLED"));
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId, String requesterEmail, boolean admin) {
        Booking booking = findBookingById(bookingId);
        if (!admin) {
            assertOwner(booking, requesterEmail);
        }
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUser(Long userId) {
        authService.getUserById(userId);
        return bookingStore.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUserEmail(String email) {
        UserDTO user = authService.getUserByEmail(email);
        return bookingStore.findByUserId(user.id());
    }

    private Booking findBookingById(Long bookingId) {
        return bookingStore.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
    }

    private void assertOwner(Booking booking, String requesterEmail) {
        UserDTO requester = authService.getUserByEmail(requesterEmail);
        if (!booking.userId().equals(requester.id())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Ngày nhận phòng và ngày trả phòng không được để trống.");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Ngày nhận phòng phải trước ngày trả phòng.");
        }
    }
}
