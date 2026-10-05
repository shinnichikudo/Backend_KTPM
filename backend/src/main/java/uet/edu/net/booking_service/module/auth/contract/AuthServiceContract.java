package uet.edu.net.booking_service.module.auth.contract;

public interface AuthServiceContract {
    /** Tìm tài khoản theo userId từ database. */
    UserDTO getUserById(Long userId);

    /** Tìm tài khoản theo email lấy từ JWT token của người dùng. */
    UserDTO getUserByEmail(String email);
}
