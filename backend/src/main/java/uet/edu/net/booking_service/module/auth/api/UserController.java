package uet.edu.net.booking_service.module.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uet.edu.net.booking_service.module.auth.api.dto.ChangePasswordRequest;
import uet.edu.net.booking_service.module.auth.contract.UserDTO;
import uet.edu.net.booking_service.module.auth.service.UserService;

/**
 * Endpoint hồ sơ và đổi mật khẩu.
 * Email luôn được lấy từ JWT principal — không tin email từ request body.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Hồ sơ và mật khẩu người dùng")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/users/me
     * Trả hồ sơ của người đang đăng nhập.
     * SecurityConfig đã bảo vệ endpoint này — không cần token sẽ nhận 401.
     */
    @GetMapping("/me")
    @Operation(summary = "Xem hồ sơ của mình")
    public ResponseEntity<UserDTO> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getProfile(authentication.getName()));
    }

    /**
     * PUT /api/users/me/password
     * Đổi mật khẩu; trả 204 No Content khi thành công.
     * 400 nếu mật khẩu cũ sai hoặc mật khẩu mới yếu.
     */
    @PutMapping("/me/password")
    @Operation(summary = "Đổi mật khẩu")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(
                authentication.getName(),
                request.oldPassword(),
                request.newPassword());
        return ResponseEntity.noContent().build();
    }
}
