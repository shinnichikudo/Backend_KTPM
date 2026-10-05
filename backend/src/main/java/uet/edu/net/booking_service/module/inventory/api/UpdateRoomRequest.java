package uet.edu.net.booking_service.module.inventory.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uet.edu.net.booking_service.module.inventory.domain.RoomType;

import java.math.BigDecimal;

public record UpdateRoomRequest(
        @NotBlank @Size(max = 30) String roomNumber,
        @NotNull RoomType roomType,
        @Min(1) int capacity,
        @NotNull @DecimalMin("0.01") BigDecimal basePrice,
        @Size(max = 1000) String description
) {
}