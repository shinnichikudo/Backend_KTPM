package uet.edu.net.booking_service.module.auth.contract;

public interface AuthServiceContract {
    /**
     * Tìm tài khoản theo email lấy từ JWT principal.
     * 
     * @param email Email của người dùng
     * @return UserDTO
     * @throws uet.edu.net.booking_service.core.exception.AppException nếu không tồn tại
     */
    UserDTO getUserByEmail(String email);
}
