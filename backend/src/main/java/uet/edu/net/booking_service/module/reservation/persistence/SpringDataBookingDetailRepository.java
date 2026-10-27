package uet.edu.net.booking_service.module.reservation.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import uet.edu.net.booking_service.module.reservation.entity.BookingDetail;

public interface SpringDataBookingDetailRepository extends JpaRepository<BookingDetail, Long> {
}
