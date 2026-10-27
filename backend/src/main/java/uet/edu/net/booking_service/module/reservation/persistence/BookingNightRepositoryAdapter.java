package uet.edu.net.booking_service.module.reservation.persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import uet.edu.net.booking_service.module.reservation.domain.RoomNightConflictException;
import uet.edu.net.booking_service.module.reservation.persistence.entity.BookingNight;
import uet.edu.net.booking_service.module.reservation.service.port.BookingNightStore;

@Repository
public class BookingNightRepositoryAdapter implements BookingNightStore {
    private final SpringDataBookingNightRepository repository;

    public BookingNightRepositoryAdapter(SpringDataBookingNightRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean hasReservation(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return repository.existsByRoomIdAndNightDateGreaterThanEqualAndNightDateLessThan(
                roomId, checkIn, checkOut);
    }

    @Override
    public void reserve(Long bookingId, Long roomId, LocalDate checkIn, LocalDate checkOut) {
        List<BookingNight> nights = new ArrayList<>();
        for (LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1)) {
            nights.add(BookingNight.builder().bookingId(bookingId).roomId(roomId).nightDate(date).build());
        }
        try {
            repository.saveAllAndFlush(nights);
        } catch (DataIntegrityViolationException exception) {
            throw new RoomNightConflictException(roomId);
        }
    }

    @Override
    public void release(Long bookingId) {
        repository.deleteByBookingId(bookingId);
    }
}
