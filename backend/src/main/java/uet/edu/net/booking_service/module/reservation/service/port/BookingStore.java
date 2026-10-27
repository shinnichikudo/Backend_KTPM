package uet.edu.net.booking_service.module.reservation.service.port;

import java.util.List;
import java.util.Optional;

import uet.edu.net.booking_service.module.reservation.domain.Booking;

public interface BookingStore {
    Optional<Booking> findById(Long bookingId);
    List<Booking> findByUserId(Long userId);
    Booking save(Booking booking);
}
