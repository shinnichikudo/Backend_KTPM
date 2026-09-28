package uet.edu.net.booking_service.module.inventory.contract;

public interface RoomServiceContract {
    /** Lấy phòng ACTIVE để tạo booking; ném AppException nếu thiếu hoặc INACTIVE. */
    RoomDTO getActiveRoomById(Long roomId);
}
