package uet.edu.net.booking_service.module.inventory.service.port;

import java.math.BigDecimal;
import java.util.List;

import uet.edu.net.booking_service.module.inventory.domain.Room;
import uet.edu.net.booking_service.module.inventory.domain.RoomType;

public interface RoomUseCases {
    List<Room> getActiveRooms(RoomType roomType, Integer minCapacity, BigDecimal minPrice, BigDecimal maxPrice);
    Room getActiveRoomById(Long id);
    Room createRoom(Room requestedRoom);
    Room updateRoom(Long id, Room requestedRoom);
    Room deactivateRoom(Long id);
}
