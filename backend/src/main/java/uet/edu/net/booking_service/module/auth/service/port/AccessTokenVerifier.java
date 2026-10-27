package uet.edu.net.booking_service.module.auth.service.port;

public interface AccessTokenVerifier {
    boolean isValid(String token);

    String extractEmail(String token);
}
