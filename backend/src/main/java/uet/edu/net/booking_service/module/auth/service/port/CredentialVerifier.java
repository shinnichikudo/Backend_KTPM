package uet.edu.net.booking_service.module.auth.service.port;

public interface CredentialVerifier {
    boolean hasValidCredentials(String email, String rawPassword);
}
