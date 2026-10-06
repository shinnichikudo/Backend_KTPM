package uet.edu.net.booking_service.module.reservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uet.edu.net.booking_service.module.reservation.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /** Lấy tất cả booking của một user, sắp xếp mới nhất lên đầu. */
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
}
