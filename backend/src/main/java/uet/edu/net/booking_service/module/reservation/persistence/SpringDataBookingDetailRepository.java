package uet.edu.net.booking_service.module.reservation.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataBookingDetailRepository extends JpaRepository<BookingDetailEntity, Long> {
}
