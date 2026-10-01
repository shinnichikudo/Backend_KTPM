package uet.edu.net.booking_service.module.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

/** Body cho PUT /api/users/me/password */
public record ChangePasswordRequest(

        @NotBlank(message = "Mật khẩu hiện tại không được để trống")
        String oldPassword,

        @NotBlank(message = "Mật khẩu mới không được để trống")
        String newPassword
) {}
