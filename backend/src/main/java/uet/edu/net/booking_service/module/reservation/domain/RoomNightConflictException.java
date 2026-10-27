package uet.edu.net.booking_service.module.reservation.domain;

public class RoomNightConflictException extends RuntimeException {
    public RoomNightConflictException(Long roomId) {
        super("Room " + roomId + " has already been reserved for one or more requested nights");
    }
}
