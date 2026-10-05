package uet.edu.net.booking_service.module.reservation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import uet.edu.net.booking_service.module.reservation.dto.BookingResponse;
import uet.edu.net.booking_service.module.reservation.dto.CreateBookingRequest;
import uet.edu.net.booking_service.module.reservation.dto.PayBookingRequest;
import uet.edu.net.booking_service.module.reservation.dto.PaymentResponse;
import uet.edu.net.booking_service.module.reservation.entity.Booking;
import uet.edu.net.booking_service.module.reservation.entity.Payment;
import uet.edu.net.booking_service.module.reservation.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponse createBooking(
            @RequestParam Long userId,
            @RequestBody CreateBookingRequest request) {
        Booking booking = bookingService.createBooking(
                userId,
                request.getRoomId(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                request.getTotalGuest());

        return mapToResponse(booking);
    }

    @GetMapping(path = "/{id}")
    public BookingResponse getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        return mapToResponse(booking);
    }

    // Xem lịch sử đặt phòng của một người dùng
    // GET /api/bookings/user/{userId}

    @GetMapping("/user/{userId}")
    public List<BookingResponse> getBookingsByUser(@PathVariable Long userId) {
        return bookingService.getBookingsByUser(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Thanh toán đơn đặt phòng (PENDING -> PAID)
    // POST /api/bookings/{id}/pay

    @PostMapping("/{id}/pay")
    public PaymentResponse payBooking(
            @PathVariable Long id,
            @Valid @RequestBody PayBookingRequest request) {
        Payment payment = bookingService.payBooking(
                id,
                request.getCardNumber(),
                request.getPaymentMethod());

        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setBookingId(payment.getBookingId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setTransactionRef(payment.getTransactionRef());
        response.setStatus(payment.getStatus());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }

    // Helper mapping
    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setUserId(booking.getUserId());
        response.setCheckInDate(booking.getCheckInDate());
        response.setCheckOutDate(booking.getCheckOutDate());
        response.setTotalGuest(booking.getTotalGuest());
        response.setTotalPrice(booking.getTotalPrice());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());

        if (booking.getBookingDetails() != null && !booking.getBookingDetails().isEmpty()) {
            response.setRoomId(booking.getBookingDetails().get(0).getRoomId());
        }
        return response;
    }
}
