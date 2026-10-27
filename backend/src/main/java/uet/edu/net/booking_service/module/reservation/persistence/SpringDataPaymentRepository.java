package uet.edu.net.booking_service.module.reservation.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import uet.edu.net.booking_service.module.reservation.persistence.entity.Payment;

public interface SpringDataPaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);
}
