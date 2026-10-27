package uet.edu.net.booking_service.module.reservation.service.port;

import java.util.Optional;

import uet.edu.net.booking_service.module.reservation.domain.Payment;
import uet.edu.net.booking_service.module.reservation.domain.PaymentAlreadyExistsException;

public interface PaymentStore {
    Optional<Payment> findByBookingId(Long bookingId);

    /** @throws PaymentAlreadyExistsException when the booking already has a payment */
    Payment save(Payment payment);
}
