package uet.edu.net.booking_service.module.reservation.persistence;

import org.springframework.stereotype.Component;

import uet.edu.net.booking_service.module.reservation.domain.Payment;

@Component
public class PaymentMapper {

    public Payment toDomain(uet.edu.net.booking_service.module.reservation.persistence.entity.Payment entity) {
        return new Payment(
                entity.getId(),
                entity.getBookingId(),
                entity.getAmount(),
                entity.getPaymentMethod(),
                entity.getTransactionRef(),
                entity.getStatus(),
                entity.getCreatedAt());
    }

    public uet.edu.net.booking_service.module.reservation.persistence.entity.Payment toEntity(Payment payment) {
        return uet.edu.net.booking_service.module.reservation.persistence.entity.Payment.builder()
                .id(payment.id())
                .bookingId(payment.bookingId())
                .amount(payment.amount())
                .paymentMethod(payment.paymentMethod())
                .transactionRef(payment.transactionRef())
                .status(payment.status())
                .createdAt(payment.createdAt())
                .build();
    }
}
