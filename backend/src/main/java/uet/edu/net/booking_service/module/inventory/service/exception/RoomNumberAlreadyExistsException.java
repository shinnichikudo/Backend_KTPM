package uet.edu.net.booking_service.module.inventory.service.exception;

public class RoomNumberAlreadyExistsException extends RuntimeException {

    public RoomNumberAlreadyExistsException(String roomNumber) {
        super("Room number already exists: " + roomNumber);
    }
}