package uet.edu.net.booking_service.module.inventory.service.exception;

public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException(Long roomId) {
        super("Room not found: " + roomId);
    }
}