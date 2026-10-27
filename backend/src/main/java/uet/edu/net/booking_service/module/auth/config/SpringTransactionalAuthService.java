package uet.edu.net.booking_service.module.auth.config;

import org.springframework.transaction.annotation.Transactional;

import uet.edu.net.booking_service.module.auth.contract.UserIdentity;
import uet.edu.net.booking_service.module.auth.service.AuthService;
import uet.edu.net.booking_service.module.auth.service.domain.AuthResult;

@Transactional
public class SpringTransactionalAuthService implements AuthService {
    private final AuthService delegate;

    public SpringTransactionalAuthService(AuthService delegate) {
        this.delegate = delegate;
    }

    @Override
    public UserIdentity register(String email, String rawPassword, String fullName, String phoneNumber) {
        return delegate.register(email, rawPassword, fullName, phoneNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResult login(String email, String rawPassword) {
        return delegate.login(email, rawPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public UserIdentity getUserByEmail(String email) {
        return delegate.getUserByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserIdentity getUserById(Long userId) {
        return delegate.getUserById(userId);
    }
}
