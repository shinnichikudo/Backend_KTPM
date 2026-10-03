package uet.edu.net.booking_service.module.inventory.domain;

import java.math.BigDecimal;

public record Room(
        Long id,
        String roomNumber,
        RoomType roomType,
        int capacity,
        BigDecimal basePrice,
        String description,
        RoomStatus status
) {

    public Room {
        if (roomNumber == null || roomNumber.isBlank()) {
            throw new IllegalArgumentException("roomNumber must not be blank");
        }
        roomNumber = roomNumber.trim();

        if (roomType == null) {
            throw new IllegalArgumentException("roomType must not be null");
        }
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be at least 1");
        }
        if (basePrice == null || basePrice.signum() <= 0) {
            throw new IllegalArgumentException("basePrice must be greater than 0");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
    }

    public Room deactivate() {
        return new Room(id, roomNumber, roomType, capacity, basePrice, description, RoomStatus.INACTIVE);
    }
}