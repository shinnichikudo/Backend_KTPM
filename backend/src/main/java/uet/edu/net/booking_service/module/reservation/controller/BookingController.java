package uet.edu.net.booking_service.module.reservation.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uet.edu.net.booking_service.module.reservation.dto.BookingResponse;
import uet.edu.net.booking_service.module.reservation.dto.CreateBookingRequest;
import uet.edu.net.booking_service.module.reservation.dto.PayBookingRequest;
import uet.edu.net.booking_service.module.reservation.dto.PaymentResponse;
import uet.edu.net.booking_service.module.reservation.domain.Booking;
import uet.edu.net.booking_service.module.reservation.domain.Payment;
import uet.edu.net.booking_service.module.reservation.service.port.BookingUseCases;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingUseCases bookingService;

    /** Creates a booking for the authenticated CUSTOMER identified by JWT email. */
    @PostMapping
    public BookingResponse createBooking(
            Authentication authentication,
            @Valid @RequestBody CreateBookingRequest request) {
        Booking booking = bookingService.createBookingByEmail(
                authentication.getName(),
                request.getRoomId(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                request.getTotalGuest());
        return mapToResponse(booking);
    }

    /** Returns a booking to its owner or an ADMIN. */
    @GetMapping("/{id}")
    public BookingResponse getBookingById(
            @PathVariable Long id,
            Authentication authentication) {
        Booking booking = bookingService.getBookingById(
                id,
                authentication.getName(),
                isAdmin(authentication));
        return mapToResponse(booking);
    }

    /** Returns the authenticated user's booking history. */
    @GetMapping("/my-bookings")
    public List<BookingResponse> getMyBookings(Authentication authentication) {
        return bookingService.getBookingsByUserEmail(authentication.getName())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /** Returns a user's bookings; SecurityConfig restricts this endpoint to ADMIN. */
    @GetMapping("/user/{userId}")
    public List<BookingResponse> getBookingsByUser(@PathVariable Long userId) {
        return bookingService.getBookingsByUser(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /** Pays a pending booking only for its owner. */
    @PostMapping("/{id}/pay")
    public PaymentResponse payBooking(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody PayBookingRequest request) {
        Payment payment = bookingService.payBooking(
                id,
                request.getCardNumber(),
                request.getPaymentMethod(),
                authentication.getName());

        PaymentResponse response = new PaymentResponse();
        response.setId(payment.id());
        response.setBookingId(payment.bookingId());
        response.setAmount(payment.amount());
        response.setPaymentMethod(payment.paymentMethod());
        response.setTransactionRef(payment.transactionRef());
        response.setStatus(payment.status());
        response.setCreatedAt(payment.createdAt());
        return response;
    }

    /** Cancels a booking for its owner or an ADMIN. */
    @DeleteMapping("/{id}")
    public BookingResponse cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {
        Booking booking = bookingService.cancelBooking(
                id,
                authentication.getName(),
                isAdmin(authentication));
        return mapToResponse(booking);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.id());
        response.setUserId(booking.userId());
        response.setCheckInDate(booking.checkInDate());
        response.setCheckOutDate(booking.checkOutDate());
        response.setTotalGuest(booking.totalGuest());
        response.setTotalPrice(booking.totalPrice());
        response.setStatus(booking.status());
        response.setCreatedAt(booking.createdAt());
        response.setRoomId(booking.roomId());
        return response;
    }
}
