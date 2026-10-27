package uet.edu.net.booking_service.module.reservation.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record Booking(Long id, Long userId, LocalDate checkInDate, LocalDate checkOutDate,
        int totalGuest, BigDecimal totalPrice, String status, LocalDateTime createdAt,
        Long roomId, BigDecimal priceAtBooking) {

    public Booking withStatus(String newStatus) {
        return new Booking(id, userId, checkInDate, checkOutDate, totalGuest,
                totalPrice, newStatus, createdAt, roomId, priceAtBooking);
    }
}
