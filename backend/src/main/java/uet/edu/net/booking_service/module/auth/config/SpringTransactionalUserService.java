package uet.edu.net.booking_service.module.auth.config;

import org.springframework.transaction.annotation.Transactional;

import uet.edu.net.booking_service.module.auth.contract.UserDTO;
import uet.edu.net.booking_service.module.auth.service.UserService;

@Transactional
public class SpringTransactionalUserService implements UserService {
    private final UserService delegate;

    public SpringTransactionalUserService(UserService delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getProfile(String email) {
        return delegate.getProfile(email);
    }

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        delegate.changePassword(email, oldPassword, newPassword);
    }
}
