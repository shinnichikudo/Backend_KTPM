package uet.edu.net.booking_service.module.auth.api.dto;

public record LoginResponse(String accessToken, String tokenType, String role) {
}
