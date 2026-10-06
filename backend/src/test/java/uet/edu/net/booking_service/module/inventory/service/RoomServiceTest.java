package uet.edu.net.booking_service.module.inventory.service;

import org.junit.jupiter.api.Test;
import uet.edu.net.booking_service.module.inventory.domain.Room;
import uet.edu.net.booking_service.module.inventory.domain.RoomStatus;
import uet.edu.net.booking_service.module.inventory.domain.RoomType;
import uet.edu.net.booking_service.module.inventory.service.exception.RoomNotFoundException;
import uet.edu.net.booking_service.module.inventory.service.exception.RoomNumberAlreadyExistsException;
import uet.edu.net.booking_service.module.inventory.service.port.RoomRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoomServiceTest {

    @Test
    void createRoomAlwaysStartsActive() {
        FakeRoomRepository repository = new FakeRoomRepository();
        RoomService service = new RoomService(repository);

        Room created = service.createRoom(room(null, "101", RoomStatus.INACTIVE));

        assertEquals(RoomStatus.ACTIVE, created.status());
        assertEquals(1L, created.id());
    }

    @Test
    void createRoomRejectsDuplicateRoomNumber() {
        FakeRoomRepository repository = new FakeRoomRepository();
        RoomService service = new RoomService(repository);
        service.createRoom(room(null, "101", RoomStatus.ACTIVE));

        assertThrows(
                RoomNumberAlreadyExistsException.class,
                () -> service.createRoom(room(null, "101", RoomStatus.ACTIVE))
        );
    }

    @Test
    void updateRoomPreservesCurrentStatus() {
        FakeRoomRepository repository = new FakeRoomRepository();
        RoomService service = new RoomService(repository);
        Room created = service.createRoom(room(null, "101", RoomStatus.ACTIVE));

        Room updated = service.updateRoom(
                created.id(),
                new Room(null, "102", RoomType.SINGLE, 1, BigDecimal.TEN, null, RoomStatus.INACTIVE)
        );

        assertEquals(RoomStatus.ACTIVE, updated.status());
        assertEquals("102", updated.roomNumber());
    }

    @Test
    void inactiveRoomIsNotReturnedByActiveLookup() {
        FakeRoomRepository repository = new FakeRoomRepository();
        RoomService service = new RoomService(repository);
        Room created = service.createRoom(room(null, "101", RoomStatus.ACTIVE));
        service.deactivateRoom(created.id());

        assertThrows(RoomNotFoundException.class, () -> service.getActiveRoomById(created.id()));
    }

    @Test
    void rejectsInvertedPriceRange() {
        RoomService service = new RoomService(new FakeRoomRepository());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.getActiveRooms(null, null, BigDecimal.TEN, BigDecimal.ONE)
        );
    }

    private Room room(Long id, String roomNumber, RoomStatus status) {
        return new Room(id, roomNumber, RoomType.SINGLE, 1, BigDecimal.TEN, null, status);
    }

    private static class FakeRoomRepository implements RoomRepository {

        private final List<Room> rooms = new ArrayList<>();
        private long nextId = 1;

        @Override
        public Optional<Room> findById(Long id) {
            return rooms.stream().filter(room -> room.id().equals(id)).findFirst();
        }

        @Override
        public List<Room> findActiveRooms(
                RoomType roomType,
                Integer minCapacity,
                BigDecimal minPrice,
                BigDecimal maxPrice
        ) {
            return rooms.stream()
                    .filter(room -> room.status() == RoomStatus.ACTIVE)
                    .filter(room -> roomType == null || room.roomType() == roomType)
                    .filter(room -> minCapacity == null || room.capacity() >= minCapacity)
                    .filter(room -> minPrice == null || room.basePrice().compareTo(minPrice) >= 0)
                    .filter(room -> maxPrice == null || room.basePrice().compareTo(maxPrice) <= 0)
                    .toList();
        }

        @Override
        public Room save(Room room) {
            Room saved = new Room(
                    room.id() == null ? nextId++ : room.id(),
                    room.roomNumber(),
                    room.roomType(),
                    room.capacity(),
                    room.basePrice(),
                    room.description(),
                    room.status()
            );
            rooms.removeIf(existing -> existing.id().equals(saved.id()));
            rooms.add(saved);
            return saved;
        }

        @Override
        public boolean existsByRoomNumber(String roomNumber) {
            return rooms.stream().anyMatch(room -> room.roomNumber().equals(roomNumber));
        }

        @Override
        public boolean existsByRoomNumberAndIdNot(String roomNumber, Long id) {
            return rooms.stream().anyMatch(room ->
                    room.roomNumber().equals(roomNumber) && !room.id().equals(id)
            );
        }
    }
}