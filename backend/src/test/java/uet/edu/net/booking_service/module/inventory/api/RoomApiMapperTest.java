package uet.edu.net.booking_service.module.inventory.api;

import org.junit.jupiter.api.Test;
import uet.edu.net.booking_service.module.inventory.domain.Room;
import uet.edu.net.booking_service.module.inventory.domain.RoomStatus;
import uet.edu.net.booking_service.module.inventory.domain.RoomType;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RoomApiMapperTest {

    private final RoomApiMapper mapper = new RoomApiMapper();

    @Test
    void mapsCreateRequestToNewActiveRoom() {
        CreateRoomRequest request = new CreateRoomRequest(
                "101",
                RoomType.SINGLE,
                1,
                new BigDecimal("500000"),
                "Quiet room"
        );

        Room room = mapper.toDomain(request);

        assertNull(room.id());
        assertEquals(RoomStatus.ACTIVE, room.status());
        assertEquals("101", room.roomNumber());
    }

    @Test
    void mapsRoomToResponse() {
        Room room = new Room(
                1L,
                "101",
                RoomType.SINGLE,
                1,
                new BigDecimal("500000"),
                "Quiet room",
                RoomStatus.ACTIVE
        );

        RoomResponse response = mapper.toResponse(room);

        assertEquals(1L, response.id());
        assertEquals(RoomType.SINGLE, response.roomType());
        assertEquals(RoomStatus.ACTIVE, response.status());
    }
}