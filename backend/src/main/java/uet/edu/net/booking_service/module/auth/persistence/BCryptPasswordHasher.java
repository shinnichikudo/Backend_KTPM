package uet.edu.net.booking_service.module.auth.persistence;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import uet.edu.net.booking_service.module.auth.service.port.PasswordHasher;

@Component
public class BCryptPasswordHasher implements PasswordHasher {
    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordHasher(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        return passwordEncoder.matches(rawPassword, passwordHash);
    }
}
