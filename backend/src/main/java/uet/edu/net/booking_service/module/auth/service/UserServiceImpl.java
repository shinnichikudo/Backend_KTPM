package uet.edu.net.booking_service.module.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uet.edu.net.booking_service.core.exception.AppException;
import uet.edu.net.booking_service.core.exception.ErrorCode;
import uet.edu.net.booking_service.module.auth.contract.UserDTO;
import uet.edu.net.booking_service.module.auth.service.domain.UserProfile;
import uet.edu.net.booking_service.module.auth.service.port.UserRepository;
import uet.edu.net.booking_service.module.auth.service.port.PasswordHasher;

import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    // Mật khẩu: tối thiểu 8 ký tự, ít nhất 1 chữ hoa, 1 chữ số
    private static final Pattern STRONG_PASSWORD = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).{8,}$");

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UserServiceImpl(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getProfile(String email) {
        UserProfile user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return new UserDTO(user.id(), user.email(), user.fullName(), user.role());
    }

    @Override
    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        UserProfile user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Xác minh mật khẩu cũ trước khi cho phép đổi
        if (!passwordHasher.matches(oldPassword, user.passwordHash())) {
            throw new AppException(ErrorCode.WRONG_OLD_PASSWORD);
        }

        // Kiểm tra độ mạnh mật khẩu mới
        if (!STRONG_PASSWORD.matcher(newPassword).matches()) {
            throw new AppException(ErrorCode.WEAK_PASSWORD);
        }

        // Tạo UserProfile mới với hash mới; JPA UPDATE vì id đã có
        UserProfile updated = new UserProfile(
                user.id(), user.email(),
                passwordHasher.hash(newPassword),
                user.fullName(), user.phoneNumber(), user.role(),
                user.createdAt(), user.updatedAt());
        userRepository.save(updated);
    }
}
