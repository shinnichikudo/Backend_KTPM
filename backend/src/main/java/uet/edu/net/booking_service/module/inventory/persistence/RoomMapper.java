package uet.edu.net.booking_service.module.inventory.persistence;

import org.springframework.stereotype.Component;
import uet.edu.net.booking_service.module.inventory.domain.Room;

@Component
public class RoomMapper {

    public Room toDomain(RoomEntity entity) {
        return new Room(
                entity.getId(),
                entity.getRoomNumber(),
                entity.getRoomType(),
                entity.getCapacity(),
                entity.getBasePrice(),
                entity.getDescription(),
                entity.getStatus()
        );
    }

    public RoomEntity toEntity(Room room) {
        RoomEntity entity = new RoomEntity();
        entity.setId(room.id());
        entity.setRoomNumber(room.roomNumber());
        entity.setRoomType(room.roomType());
        entity.setCapacity(room.capacity());
        entity.setBasePrice(room.basePrice());
        entity.setDescription(room.description());
        entity.setStatus(room.status());
        return entity;
    }
}