package uet.edu.net.booking_service.module.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uet.edu.net.booking_service.module.reservation.entity.BookingNight;

import java.time.LocalDate;

@Repository
public interface BookingNightRepository extends JpaRepository<BookingNight, Long> {

    boolean existsByRoomIdAndNightDateGreaterThanEqualAndNightDateLessThan(
            Long roomId, LocalDate checkIn, LocalDate checkOut);

    @Modifying
    @Query("DELETE FROM BookingNight bn WHERE bn.bookingId = :bookingId")
    void deleteByBookingId(@Param("bookingId") Long bookingId);
}

