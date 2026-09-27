package uet.edu.net.booking_service.module.auth.contract;

public interface AuthServiceContract {
    /**
     * Lấy thông tin tài khoản an toàn qua DTO.
     * 
     * @param userId Mã định danh của người dùng
     * @return UserDTO
     * @throws RuntimeException nếu ID không tồn tại
     */
    UserDTO getUserById(Long userId);
}
