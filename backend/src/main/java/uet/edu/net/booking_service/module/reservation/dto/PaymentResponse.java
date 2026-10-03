package uet.edu.net.booking_service.module.reservation.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {

    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionRef;
    private String status;
    private LocalDateTime createdAt;
}

