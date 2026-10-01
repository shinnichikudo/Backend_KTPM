package uet.edu.net.booking_service.module.reservation.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponse {
    
    private Long id;

    private Long userId;

    private Long roomId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private int totalGuest;

    private BigDecimal totalPrice;

    private String status;

    private LocalDateTime createdAt;
}
