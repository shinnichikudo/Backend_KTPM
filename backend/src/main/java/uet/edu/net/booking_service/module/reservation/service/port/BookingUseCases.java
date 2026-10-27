package uet.edu.net.booking_service.module.reservation.service.port;

import java.time.LocalDate;
import java.util.List;

import uet.edu.net.booking_service.module.reservation.domain.Booking;
import uet.edu.net.booking_service.module.reservation.domain.Payment;

public interface BookingUseCases {
    Booking createBookingByEmail(String email, Long roomId, LocalDate checkIn, LocalDate checkOut, int totalGuest);
    Booking getBookingById(Long bookingId, String requesterEmail, boolean admin);
    List<Booking> getBookingsByUser(Long userId);
    List<Booking> getBookingsByUserEmail(String email);
    Payment payBooking(Long bookingId, String cardNumber, String paymentMethod, String requesterEmail);
    Booking cancelBooking(Long bookingId, String requesterEmail, boolean admin);
}
