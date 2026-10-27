package uet.edu.net.booking_service.module.reservation.config;

import java.time.LocalDate;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import uet.edu.net.booking_service.module.reservation.domain.Booking;
import uet.edu.net.booking_service.module.reservation.domain.Payment;
import uet.edu.net.booking_service.module.reservation.service.port.BookingUseCases;

@Transactional
public class SpringTransactionalBookingUseCases implements BookingUseCases {
    private final BookingUseCases delegate;

    public SpringTransactionalBookingUseCases(BookingUseCases delegate) {
        this.delegate = delegate;
    }

    @Override
    public Booking createBookingByEmail(String email, Long roomId, LocalDate checkIn, LocalDate checkOut,
            int totalGuest) {
        return delegate.createBookingByEmail(email, roomId, checkIn, checkOut, totalGuest);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId, String requesterEmail, boolean admin) {
        return delegate.getBookingById(bookingId, requesterEmail, admin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUser(Long userId) {
        return delegate.getBookingsByUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUserEmail(String email) {
        return delegate.getBookingsByUserEmail(email);
    }

    @Override
    public Payment payBooking(Long bookingId, String cardNumber, String paymentMethod, String requesterEmail) {
        return delegate.payBooking(bookingId, cardNumber, paymentMethod, requesterEmail);
    }

    @Override
    public Booking cancelBooking(Long bookingId, String requesterEmail, boolean admin) {
        return delegate.cancelBooking(bookingId, requesterEmail, admin);
    }
}
