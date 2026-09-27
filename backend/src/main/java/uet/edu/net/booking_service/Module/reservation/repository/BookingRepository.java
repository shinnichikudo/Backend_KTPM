package uet.edu.net.booking_service.Module.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uet.edu.net.booking_service.Module.reservation.entity.Booking;

import java.time.LocalDate;
import java.util.List;

@Repository

public interface BookingRepository extends JpaRepository<Booking, Long> {

  /**
   * Tìm tất cả ID phòng đã được đặt trong khoảng thời gian chỉ định.
   * Điều kiện giao cắt: checkIn < existingCheckOut AND checkOut > existingCheckIn
   */
  @Query("""
      SELECT DISTINCT bd.roomId
      FROM Booking b
      JOIN b.bookingDetails bd
      WHERE b.status IN ('PENDING', 'PAID')
        AND b.checkInDate  < :checkOut
        AND b.checkOutDate > :checkIn
      """)
  List<Long> findBookedRoomIds(@Param("checkIn") LocalDate checkIn,
      @Param("checkOut") LocalDate checkOut);

  /** Lấy tất cả booking của một user, sắp xếp mới nhất lên đầu. */
  List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
}
