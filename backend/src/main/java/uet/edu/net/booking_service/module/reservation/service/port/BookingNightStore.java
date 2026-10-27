package uet.edu.net.booking_service.module.reservation.service.port;

import java.time.LocalDate;

public interface BookingNightStore {
    boolean hasReservation(Long roomId, LocalDate checkIn, LocalDate checkOut);
    void reserve(Long bookingId, Long roomId, LocalDate checkIn, LocalDate checkOut);
    void release(Long bookingId);
}
