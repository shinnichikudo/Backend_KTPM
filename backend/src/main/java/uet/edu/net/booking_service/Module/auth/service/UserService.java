package uet.edu.net.booking_service.module.auth.service;

import uet.edu.net.booking_service.module.auth.contract.UserDTO;

/** Use cases cho hồ sơ và mật khẩu của người dùng đã đăng nhập. */
public interface UserService {

    /** Trả thông tin hồ sơ của user theo email từ JWT principal. */
    UserDTO getProfile(String email);

    /**
     * Đổi mật khẩu.
     * Ném WRONG_OLD_PASSWORD nếu oldPassword không khớp.
     * Ném WEAK_PASSWORD nếu newPassword không đủ mạnh.
     */
    void changePassword(String email, String oldPassword, String newPassword);
}
