package uet.edu.net.booking_service.Module.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uet.edu.net.booking_service.Module.reservation.entity.Booking;
import uet.edu.net.booking_service.Module.reservation.entity.BookingDetail;
@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long> {
}
