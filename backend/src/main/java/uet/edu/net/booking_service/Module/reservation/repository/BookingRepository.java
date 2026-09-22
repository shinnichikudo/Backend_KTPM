package uet.edu.net.booking_service.Module.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uet.edu.net.booking_service.Module.reservation.entity.Booking;
@Repository

public interface BookingRepository extends JpaRepository<Booking, Long> {

}
