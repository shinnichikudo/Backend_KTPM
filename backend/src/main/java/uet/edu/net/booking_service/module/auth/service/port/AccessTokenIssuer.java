package uet.edu.net.booking_service.module.auth.service.port;

public interface AccessTokenIssuer {
    String issue(String email, String role);
}
