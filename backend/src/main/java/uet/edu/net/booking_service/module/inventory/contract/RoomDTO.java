package uet.edu.net.booking_service.module.inventory.contract;

import java.math.BigDecimal;

public record RoomDTO(
        Long id,
        String roomNumber,
        String roomType,
        BigDecimal basePrice,
        int capacity,
        String status
) {
}
