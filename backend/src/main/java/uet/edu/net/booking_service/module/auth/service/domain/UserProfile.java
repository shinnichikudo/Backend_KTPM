package uet.edu.net.booking_service.module.auth.service.domain;

import java.time.LocalDateTime;

/** Framework-independent user model used by the auth use cases. */
public record UserProfile(
        Long id,
        String email,
        String passwordHash,
        String fullName,
        String phoneNumber,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
