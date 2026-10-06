package uet.edu.net.booking_service.module.inventory.api;

import uet.edu.net.booking_service.module.inventory.domain.RoomStatus;
import uet.edu.net.booking_service.module.inventory.domain.RoomType;

import java.math.BigDecimal;

public record RoomResponse(
        Long id,
        String roomNumber,
        RoomType roomType,
        int capacity,
        BigDecimal basePrice,
        String description,
        RoomStatus status
) {
}