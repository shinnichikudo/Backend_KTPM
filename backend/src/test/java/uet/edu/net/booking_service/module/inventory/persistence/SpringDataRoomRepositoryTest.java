package uet.edu.net.booking_service.module.inventory.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import uet.edu.net.booking_service.module.inventory.domain.Room;
import uet.edu.net.booking_service.module.inventory.domain.RoomStatus;
import uet.edu.net.booking_service.module.inventory.domain.RoomType;
import uet.edu.net.booking_service.module.inventory.service.port.RoomRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:inventory;DB_CLOSE_DELAY=-1",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
@Transactional
class SpringDataRoomRepositoryTest {

    @Autowired
    private SpringDataRoomRepository repository;

        @Autowired
        private RoomRepository roomRepository;

        @Test
        void adapterPersistsAndReadsDomainRoom() {
                Room saved = roomRepository.save(new Room(
                                null,
                                "501",
                                RoomType.SUITE,
                                3,
                                new BigDecimal("1500000"),
                                "Corner suite",
                                RoomStatus.ACTIVE
                ));

                Room loaded = roomRepository.findById(saved.id()).orElseThrow();

                assertNotNull(saved.id());
                assertEquals("501", loaded.roomNumber());
                assertEquals(RoomType.SUITE, loaded.roomType());
                assertTrue(roomRepository.existsByRoomNumber("501"));
        }

    @Test
    void filtersActiveRoomsByTypeCapacityAndPrice() {
        repository.save(room("101", RoomType.SINGLE, 1, "500000", RoomStatus.ACTIVE));
        repository.save(room("201", RoomType.DOUBLE, 2, "800000", RoomStatus.ACTIVE));
        repository.save(room("301", RoomType.DOUBLE, 4, "1000000", RoomStatus.INACTIVE));

        List<RoomEntity> result = repository.findActiveRooms(
                RoomStatus.ACTIVE,
                RoomType.DOUBLE,
                2,
                new BigDecimal("700000"),
                new BigDecimal("900000")
        );

        assertEquals(List.of("201"), result.stream().map(RoomEntity::getRoomNumber).toList());
    }

    @Test
    void includesAllActiveRoomsWhenFiltersAreAbsentAndSortsByNumber() {
        repository.save(room("203", RoomType.DOUBLE, 2, "800000", RoomStatus.ACTIVE));
        repository.save(room("101", RoomType.SINGLE, 1, "500000", RoomStatus.ACTIVE));
        repository.save(room("001", RoomType.SINGLE, 1, "400000", RoomStatus.INACTIVE));

        List<RoomEntity> result = repository.findActiveRooms(
                RoomStatus.ACTIVE,
                null,
                null,
                null,
                null
        );

        assertEquals(List.of("101", "203"), result.stream().map(RoomEntity::getRoomNumber).toList());
    }

    private RoomEntity room(
            String roomNumber,
            RoomType roomType,
            int capacity,
            String price,
            RoomStatus status
    ) {
        RoomEntity entity = new RoomEntity();
        entity.setRoomNumber(roomNumber);
        entity.setRoomType(roomType);
        entity.setCapacity(capacity);
        entity.setBasePrice(new BigDecimal(price));
        entity.setStatus(status);
        return entity;
    }
}