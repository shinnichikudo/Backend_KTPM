package uet.edu.net.booking_service.module.auth.service.domain;

/** Result of a successful authentication, independent of the HTTP response DTO. */
public record AuthResult(String accessToken, String role) {
}
