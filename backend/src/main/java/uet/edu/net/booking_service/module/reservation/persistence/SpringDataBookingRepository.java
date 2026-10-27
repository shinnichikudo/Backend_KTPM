package uet.edu.net.booking_service.module.reservation.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import uet.edu.net.booking_service.module.reservation.entity.Booking;

public interface SpringDataBookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
}
