package uet.edu.net.booking_service.module.reservation.domain;

public class PaymentAlreadyExistsException extends RuntimeException {
    public PaymentAlreadyExistsException(Long bookingId) {
        super("A payment already exists for booking " + bookingId);
    }
}
