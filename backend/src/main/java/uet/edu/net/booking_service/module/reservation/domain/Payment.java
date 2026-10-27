package uet.edu.net.booking_service.module.reservation.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Payment(
        Long id,
        Long bookingId,
        BigDecimal amount,
        String paymentMethod,
        String transactionRef,
        String status,
        LocalDateTime createdAt) {
}
