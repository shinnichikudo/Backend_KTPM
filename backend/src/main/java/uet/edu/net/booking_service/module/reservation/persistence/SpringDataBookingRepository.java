package uet.edu.net.booking_service.module.reservation.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataBookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}
