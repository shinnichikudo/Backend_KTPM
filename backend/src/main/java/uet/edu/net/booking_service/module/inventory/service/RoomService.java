package uet.edu.net.booking_service.module.inventory.service;

import org.springframework.stereotype.Service;
import uet.edu.net.booking_service.module.inventory.contract.RoomDTO;
import uet.edu.net.booking_service.module.inventory.contract.RoomServiceContract;

import java.math.BigDecimal;

/**
 * Implementation của RoomServiceContract cho module Inventory.
 * Trong kiến trúc Modular Monolith, class này đóng vai trò
 * là cổng giao tiếp nội bộ giữa module Reservation và module Inventory.
 */
@Service
public class RoomService implements RoomServiceContract {

    @Override
    public RoomDTO getRoomById(Long roomId) {
        // TODO: Thay thế bằng logic thực tế (truy vấn RoomRepository)
        if (roomId == null || roomId <= 0) {
            throw new RuntimeException("Không tìm thấy phòng với ID: " + roomId);
        }
        // Placeholder — trả về RoomDTO giả để service hoạt động
        return new RoomDTO(roomId, "P" + roomId, new BigDecimal("500000"), 2, "AVAILABLE");
    }

    @Override
    public RoomDTO getRoomForUpdate(Long roomId) {
        // TODO: Thay thế bằng truy vấn với Pessimistic Lock (PESSIMISTIC_WRITE)
        // Ví dụ: roomRepository.findByIdWithLock(roomId)
        return getRoomById(roomId);
    }
}
