package uet.edu.net.booking_service.module.inventory.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoomTest {

    @Test
    void trimsRoomNumber() {
        Room room = room(" 101 ", 2, new BigDecimal("500000"));

        assertEquals("101", room.roomNumber());
    }

    @Test
    void rejectsNonPositiveCapacity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> room("101", 0, new BigDecimal("500000"))
        );
    }

    @Test
    void rejectsNonPositivePrice() {
        assertThrows(
                IllegalArgumentException.class,
                () -> room("101", 2, BigDecimal.ZERO)
        );
    }

    @Test
    void deactivatePreservesRoomDetails() {
        Room room = room("101", 2, new BigDecimal("500000"));

        Room inactiveRoom = room.deactivate();

        assertEquals(RoomStatus.INACTIVE, inactiveRoom.status());
        assertEquals(room.roomNumber(), inactiveRoom.roomNumber());
        assertEquals(room.basePrice(), inactiveRoom.basePrice());
    }

    private Room room(String roomNumber, int capacity, BigDecimal basePrice) {
        return new Room(
                1L,
                roomNumber,
                RoomType.DOUBLE,
                capacity,
                basePrice,
                "Test room",
                RoomStatus.ACTIVE
        );
    }
}