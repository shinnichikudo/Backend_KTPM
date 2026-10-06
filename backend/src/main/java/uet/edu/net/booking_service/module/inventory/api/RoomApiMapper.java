package uet.edu.net.booking_service.module.inventory.api;

import org.springframework.stereotype.Component;
import uet.edu.net.booking_service.module.inventory.domain.Room;
import uet.edu.net.booking_service.module.inventory.domain.RoomStatus;

@Component
public class RoomApiMapper {

    public Room toDomain(CreateRoomRequest request) {
        return new Room(
                null,
                request.roomNumber(),
                request.roomType(),
                request.capacity(),
                request.basePrice(),
                request.description(),
                RoomStatus.ACTIVE
        );
    }

    public Room toDomain(UpdateRoomRequest request) {
        return new Room(
                null,
                request.roomNumber(),
                request.roomType(),
                request.capacity(),
                request.basePrice(),
                request.description(),
                RoomStatus.ACTIVE
        );
    }

    public RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.id(),
                room.roomNumber(),
                room.roomType(),
                room.capacity(),
                room.basePrice(),
                room.description(),
                room.status()
        );
    }
}