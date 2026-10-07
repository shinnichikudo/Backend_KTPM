package uet.edu.net.booking_service.module.reservation.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateBookingRequest {
    @NotNull(message = "roomId không được để trống")
    @Positive(message = "roomId phải lớn hơn 0")
    private Long roomId;

    @NotNull(message = "checkInDate không được để trống")
    private LocalDate checkInDate;

    @NotNull(message = "checkOutDate không được để trống")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "totalGuest phải ít nhất là 1")
    private int totalGuest;

    @AssertTrue(message = "checkInDate phải trước checkOutDate")
    public boolean isDateRangeValid() {
        return checkInDate == null || checkOutDate == null || checkInDate.isBefore(checkOutDate);
    }
}
