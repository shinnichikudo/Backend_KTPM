package uet.edu.net.booking_service.module.reservation.contract;

import java.time.LocalDate;
import java.util.List;

public interface BookingServiceContract {
    /**
     * Tìm tất cả ID phòng đang nằm trong các đơn đặt phòng hợp lệ (PENDING, PAID)
     * và có thời gian lưu trú giao cắt với khoảng thời gian khách muốn đặt.
     *
     * @param checkIn  Ngày đến dự kiến
     * @param checkOut Ngày đi dự kiến
     * @return Danh sách ID phòng KHÔNG khả dụng
     */
    List<Long> getBookedRoomIds(LocalDate checkIn, LocalDate checkOut);
}
