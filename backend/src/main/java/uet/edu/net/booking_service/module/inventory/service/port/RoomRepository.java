package uet.edu.net.booking_service.module.inventory.service.port;

import uet.edu.net.booking_service.module.inventory.domain.Room;
import uet.edu.net.booking_service.module.inventory.domain.RoomType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RoomRepository {

    Optional<Room> findById(Long id);

    List<Room> findActiveRooms(
            RoomType roomType,
            Integer minCapacity,
            BigDecimal minPrice,
            BigDecimal maxPrice
    );

    Room save(Room room);

    boolean existsByRoomNumber(String roomNumber);

    boolean existsByRoomNumberAndIdNot(String roomNumber, Long id);
}