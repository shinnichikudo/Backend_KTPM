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
import uet.edu.net.booking_service.module.reservation.entity.Booking;
import uet.edu.net.booking_service.module.reservation.entity.Payment;
import uet.edu.net.booking_service.module.reservation.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

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

    /** Pays a pending booking for its owner or an ADMIN. */
    @PostMapping("/{id}/pay")
    public PaymentResponse payBooking(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody PayBookingRequest request) {
        Payment payment = bookingService.payBooking(
                id,
                request.getCardNumber(),
                request.getPaymentMethod(),
                authentication.getName(),
                isAdmin(authentication));

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
