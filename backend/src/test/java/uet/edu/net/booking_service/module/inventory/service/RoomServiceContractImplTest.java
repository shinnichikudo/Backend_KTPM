package uet.edu.net.booking_service.module.inventory.service;

import org.junit.jupiter.api.Test;
import uet.edu.net.booking_service.module.inventory.contract.RoomDTO;
import uet.edu.net.booking_service.module.inventory.domain.Room;
import uet.edu.net.booking_service.module.inventory.domain.RoomStatus;
import uet.edu.net.booking_service.module.inventory.domain.RoomType;
import uet.edu.net.booking_service.module.inventory.service.exception.RoomNotFoundException;
import uet.edu.net.booking_service.module.inventory.service.port.RoomRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoomServiceContractImplTest {

    @Test
    void returnsActiveRoomAsContractDto() {
        RoomService service = new RoomService(new SingleRoomRepository(
                new Room(8L, "208", RoomType.DOUBLE, 2, new BigDecimal("850000"), "Balcony", RoomStatus.ACTIVE)
        ));
        RoomServiceContractImpl contract = new RoomServiceContractImpl(service);

        RoomDTO room = contract.getActiveRoomById(8L);

        assertEquals(8L, room.id());
        assertEquals("208", room.roomNumber());
        assertEquals("DOUBLE", room.roomType());
        assertEquals(new BigDecimal("850000"), room.basePrice());
        assertEquals(2, room.capacity());
        assertEquals("ACTIVE", room.status());
    }

    @Test
    void doesNotExposeInactiveRoomToReservation() {
        RoomService service = new RoomService(new SingleRoomRepository(
                new Room(8L, "208", RoomType.DOUBLE, 2, new BigDecimal("850000"), null, RoomStatus.INACTIVE)
        ));
        RoomServiceContractImpl contract = new RoomServiceContractImpl(service);

        assertThrows(RoomNotFoundException.class, () -> contract.getActiveRoomById(8L));
    }

    private record SingleRoomRepository(Room room) implements RoomRepository {

        @Override
        public Optional<Room> findById(Long id) {
            return room.id().equals(id) ? Optional.of(room) : Optional.empty();
        }

        @Override
        public java.util.List<Room> findActiveRooms(
                RoomType roomType,
                Integer minCapacity,
                BigDecimal minPrice,
                BigDecimal maxPrice
        ) {
            return java.util.List.of();
        }

        @Override
        public Room save(Room roomToSave) {
            return roomToSave;
        }

        @Override
        public boolean existsByRoomNumber(String roomNumber) {
            return false;
        }

        @Override
        public boolean existsByRoomNumberAndIdNot(String roomNumber, Long id) {
            return false;
        }
    }
}