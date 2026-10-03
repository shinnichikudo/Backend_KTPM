package uet.edu.net.booking_service.Module.inventory.contract;

public interface RoomServiceContract {
    /**
     * Lấy thông tin phòng thông thường (Chỉ đọc).
     */
    RoomDTO getRoomById(Long roomId);

    /**
     * Lấy thông tin phòng và KÍCH HOẠT KHÓA (Pessimistic Lock) ở Database.
     * Dùng riêng cho luồng tạo đơn đặt phòng để chống Double Booking.
     */
    RoomDTO getRoomForUpdate(Long roomId);
}
