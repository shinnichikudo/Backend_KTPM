package uet.edu.net.booking_service.module.auth.persistence;

import org.springframework.stereotype.Component;

import uet.edu.net.booking_service.module.auth.service.port.AccessTokenIssuer;
import uet.edu.net.booking_service.module.auth.service.port.AccessTokenVerifier;

@Component
public class JwtAccessTokenIssuer implements AccessTokenIssuer, AccessTokenVerifier {
    private final JwtTokenService jwtTokenService;

    public JwtAccessTokenIssuer(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public String issue(String email, String role) {
        return jwtTokenService.generateToken(email, role);
    }

    @Override
    public boolean isValid(String token) {
        return jwtTokenService.isValid(token);
    }

    @Override
    public String extractEmail(String token) {
        return jwtTokenService.extractEmail(token);
    }
}
