package uet.edu.net.booking_service.module.auth.api.dto;

/** HTTP response representation for a user's public profile. */
public record UserResponse(Long id, String email, String fullName, String role) {
}
