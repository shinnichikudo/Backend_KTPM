package uet.edu.net.booking_service.module.inventory.api;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uet.edu.net.booking_service.module.inventory.domain.Room;
import uet.edu.net.booking_service.module.inventory.domain.RoomStatus;
import uet.edu.net.booking_service.module.inventory.service.RoomService;
import uet.edu.net.booking_service.module.inventory.service.exception.RoomNotFoundException;
import uet.edu.net.booking_service.module.inventory.service.port.RoomRepository;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoomExceptionHandlerTest {

    @Test
    void missingRoomReturnsNotFoundError() throws Exception {
        RoomRepository missingRooms = new RoomRepository() {
            @Override
            public Optional<Room> findById(Long id) {
                return Optional.empty();
            }

            @Override
            public java.util.List<Room> findActiveRooms(
                    uet.edu.net.booking_service.module.inventory.domain.RoomType roomType,
                    Integer minCapacity,
                    java.math.BigDecimal minPrice,
                    java.math.BigDecimal maxPrice
            ) {
                return java.util.List.of();
            }

            @Override
            public Room save(Room room) {
                return room;
            }

            @Override
            public boolean existsByRoomNumber(String roomNumber) {
                return false;
            }

            @Override
            public boolean existsByRoomNumberAndIdNot(String roomNumber, Long id) {
                return false;
            }
        };
        RoomController controller = new RoomController(new RoomService(missingRooms), new RoomApiMapper());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RoomExceptionHandler())
                .build();

        mockMvc.perform(get("/api/rooms/17"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ROOM_NOT_FOUND"));
    }
}