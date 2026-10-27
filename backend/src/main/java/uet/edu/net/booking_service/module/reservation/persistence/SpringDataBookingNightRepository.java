package uet.edu.net.booking_service.module.reservation.persistence;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uet.edu.net.booking_service.module.reservation.entity.BookingNight;

public interface SpringDataBookingNightRepository extends JpaRepository<BookingNight, Long> {
    boolean existsByRoomIdAndNightDateGreaterThanEqualAndNightDateLessThan(
            Long roomId, LocalDate checkIn, LocalDate checkOut);

    @Modifying
    @Query("DELETE FROM BookingNight bn WHERE bn.bookingId = :bookingId")
    void deleteByBookingId(@Param("bookingId") Long bookingId);
}
