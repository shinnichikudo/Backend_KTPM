package uet.edu.net.booking_service.module.auth.config;

import org.springframework.transaction.annotation.Transactional;

import uet.edu.net.booking_service.module.auth.contract.UserDTO;
import uet.edu.net.booking_service.module.auth.service.AuthService;
import uet.edu.net.booking_service.module.auth.service.domain.AuthResult;

@Transactional
public class SpringTransactionalAuthService implements AuthService {
    private final AuthService delegate;

    public SpringTransactionalAuthService(AuthService delegate) {
        this.delegate = delegate;
    }

    @Override
    public UserDTO register(String email, String rawPassword, String fullName, String phoneNumber) {
        return delegate.register(email, rawPassword, fullName, phoneNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResult login(String email, String rawPassword) {
        return delegate.login(email, rawPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        return delegate.getUserByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        return delegate.getUserById(userId);
    }
}
