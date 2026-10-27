package uet.edu.net.booking_service.module.reservation.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface SpringDataPaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByBookingId(Long bookingId);
}
