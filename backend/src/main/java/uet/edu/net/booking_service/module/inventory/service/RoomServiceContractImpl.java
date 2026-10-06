package uet.edu.net.booking_service.module.inventory.service;

import org.springframework.stereotype.Component;
import uet.edu.net.booking_service.module.inventory.contract.RoomDTO;
import uet.edu.net.booking_service.module.inventory.contract.RoomServiceContract;
import uet.edu.net.booking_service.module.inventory.domain.Room;

@Component
public class RoomServiceContractImpl implements RoomServiceContract {

    private final RoomService roomService;

    public RoomServiceContractImpl(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public RoomDTO getActiveRoomById(Long roomId) {
        Room room = roomService.getActiveRoomById(roomId);
        return new RoomDTO(
                room.id(),
                room.roomNumber(),
                room.roomType().name(),
                room.basePrice(),
                room.capacity(),
                room.status().name()
        );
    }
}