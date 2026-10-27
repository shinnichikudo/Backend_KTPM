package uet.edu.net.booking_service.module.auth.persistence;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import uet.edu.net.booking_service.module.auth.service.port.CredentialVerifier;

@Component
public class SpringSecurityCredentialVerifier implements CredentialVerifier {
    private final AuthenticationManager authenticationManager;

    public SpringSecurityCredentialVerifier(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public boolean hasValidCredentials(String email, String rawPassword) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, rawPassword));
            return true;
        } catch (AuthenticationException exception) {
            return false;
        }
    }
}
