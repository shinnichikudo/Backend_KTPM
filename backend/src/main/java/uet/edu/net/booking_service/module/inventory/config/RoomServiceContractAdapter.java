package uet.edu.net.booking_service.module.inventory.config;

import org.springframework.stereotype.Component;
import uet.edu.net.booking_service.module.inventory.contract.RoomDTO;
import uet.edu.net.booking_service.module.inventory.contract.RoomServiceContract;
import uet.edu.net.booking_service.module.inventory.domain.Room;
import uet.edu.net.booking_service.module.inventory.service.port.RoomUseCases;

@Component
public class RoomServiceContractAdapter implements RoomServiceContract {
    private final RoomUseCases roomUseCases;

    public RoomServiceContractAdapter(RoomUseCases roomUseCases) {
        this.roomUseCases = roomUseCases;
    }

    @Override
    public RoomDTO getActiveRoomById(Long roomId) {
        Room room = roomUseCases.getActiveRoomById(roomId);
        return new RoomDTO(
                room.id(), room.roomNumber(), room.roomType().name(),
                room.basePrice(), room.capacity(), room.status().name());
    }
}
