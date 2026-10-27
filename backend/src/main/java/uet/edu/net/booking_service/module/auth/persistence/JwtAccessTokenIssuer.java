package uet.edu.net.booking_service.module.auth.persistence;

import org.springframework.stereotype.Component;

import uet.edu.net.booking_service.core.security.JwtUtils;
import uet.edu.net.booking_service.module.auth.service.port.AccessTokenIssuer;

@Component
public class JwtAccessTokenIssuer implements AccessTokenIssuer {
    private final JwtUtils jwtUtils;

    public JwtAccessTokenIssuer(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public String issue(String email, String role) {
        return jwtUtils.generateToken(email, role);
    }
}
