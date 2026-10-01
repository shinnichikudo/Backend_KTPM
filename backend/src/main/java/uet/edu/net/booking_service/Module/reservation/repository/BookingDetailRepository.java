package uet.edu.net.booking_service.module.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uet.edu.net.booking_service.module.reservation.entity.BookingDetail;
@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long> {
}