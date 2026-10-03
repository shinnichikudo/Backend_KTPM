package uet.edu.net.booking_service.module.inventory.persistence;

import org.junit.jupiter.api.Test;
import uet.edu.net.booking_service.module.inventory.domain.Room;
import uet.edu.net.booking_service.module.inventory.domain.RoomStatus;
import uet.edu.net.booking_service.module.inventory.domain.RoomType;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoomMapperTest {

    private final RoomMapper mapper = new RoomMapper();

    @Test
    void mapsEntityToDomain() {
        RoomEntity entity = new RoomEntity();
        entity.setId(7L);
        entity.setRoomNumber("207");
        entity.setRoomType(RoomType.DOUBLE);
        entity.setCapacity(2);
        entity.setBasePrice(new BigDecimal("800000"));
        entity.setDescription("City view");
        entity.setStatus(RoomStatus.ACTIVE);

        Room room = mapper.toDomain(entity);

        assertEquals(7L, room.id());
        assertEquals("207", room.roomNumber());
        assertEquals(RoomType.DOUBLE, room.roomType());
        assertEquals(new BigDecimal("800000"), room.basePrice());
    }

    @Test
    void mapsDomainToEntity() {
        Room room = new Room(
                7L,
                "207",
                RoomType.DOUBLE,
                2,
                new BigDecimal("800000"),
                "City view",
                RoomStatus.INACTIVE
        );

        RoomEntity entity = mapper.toEntity(room);

        assertEquals(7L, entity.getId());
        assertEquals("207", entity.getRoomNumber());
        assertEquals(RoomStatus.INACTIVE, entity.getStatus());
    }
}