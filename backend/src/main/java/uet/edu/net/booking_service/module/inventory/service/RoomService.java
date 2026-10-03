package uet.edu.net.booking_service.module.inventory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uet.edu.net.booking_service.module.inventory.domain.Room;
import uet.edu.net.booking_service.module.inventory.domain.RoomStatus;
import uet.edu.net.booking_service.module.inventory.domain.RoomType;
import uet.edu.net.booking_service.module.inventory.service.exception.RoomNotFoundException;
import uet.edu.net.booking_service.module.inventory.service.exception.RoomNumberAlreadyExistsException;
import uet.edu.net.booking_service.module.inventory.service.port.RoomRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Transactional(readOnly = true)
    public List<Room> getActiveRooms(
            RoomType roomType,
            Integer minCapacity,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        if (minCapacity != null && minCapacity < 1) {
            throw new IllegalArgumentException("minCapacity must be at least 1");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice must not exceed maxPrice");
        }

        return roomRepository.findActiveRooms(roomType, minCapacity, minPrice, maxPrice);
    }

    @Transactional(readOnly = true)
    public Room getActiveRoomById(Long id) {
        Room room = findById(id);
        if (room.status() != RoomStatus.ACTIVE) {
            throw new RoomNotFoundException(id);
        }
        return room;
    }

    public Room createRoom(Room requestedRoom) {
        if (roomRepository.existsByRoomNumber(requestedRoom.roomNumber())) {
            throw new RoomNumberAlreadyExistsException(requestedRoom.roomNumber());
        }

        Room room = new Room(
                null,
                requestedRoom.roomNumber(),
                requestedRoom.roomType(),
                requestedRoom.capacity(),
                requestedRoom.basePrice(),
                requestedRoom.description(),
                RoomStatus.ACTIVE
        );
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room requestedRoom) {
        Room existingRoom = findById(id);
        if (roomRepository.existsByRoomNumberAndIdNot(requestedRoom.roomNumber(), id)) {
            throw new RoomNumberAlreadyExistsException(requestedRoom.roomNumber());
        }

        Room updatedRoom = new Room(
                existingRoom.id(),
                requestedRoom.roomNumber(),
                requestedRoom.roomType(),
                requestedRoom.capacity(),
                requestedRoom.basePrice(),
                requestedRoom.description(),
                existingRoom.status()
        );
        return roomRepository.save(updatedRoom);
    }

    public Room deactivateRoom(Long id) {
        return roomRepository.save(findById(id).deactivate());
    }

    private Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));
    }
}