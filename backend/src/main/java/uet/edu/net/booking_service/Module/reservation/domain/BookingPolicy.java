package uet.edu.net.booking_service.Module.reservation.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingPolicy {
    public Long caculateValidDay(LocalDate checkIn , LocalDate checkOut) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        if (days < 0 ) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        } else {
            return days;
        }
    }
    // tinh tong tien
    public BigDecimal caculateTotalPrice (BigDecimal basePrice, Long days) {
        if (days < 0 ) {
            throw new IllegalArgumentException("Days must be greater than 0");
        } else {
            return basePrice.multiply(BigDecimal.valueOf(days));
        }
    }
    // dieu kien de duoc huy don
    public boolean canCancel(String currStatus) {
        return currStatus.equals("PENDING");
    }
}