package uet.edu.net.booking_service.module.reservation.persistence;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataBookingNightRepository extends JpaRepository<BookingNightEntity, Long> {
    boolean existsByRoomIdAndNightDateGreaterThanEqualAndNightDateLessThan(
            Long roomId, LocalDate checkIn, LocalDate checkOut);

    @Modifying
    @Query("DELETE FROM BookingNightEntity bn WHERE bn.bookingId = :bookingId")
    void deleteByBookingId(@Param("bookingId") Long bookingId);
}
