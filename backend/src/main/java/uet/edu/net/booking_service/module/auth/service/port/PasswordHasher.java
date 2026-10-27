package uet.edu.net.booking_service.module.auth.service.port;

public interface PasswordHasher {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String passwordHash);
}
