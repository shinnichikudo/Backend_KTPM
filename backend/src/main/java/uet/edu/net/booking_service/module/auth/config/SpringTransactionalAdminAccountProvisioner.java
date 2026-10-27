package uet.edu.net.booking_service.module.auth.config;

import org.springframework.transaction.annotation.Transactional;

import uet.edu.net.booking_service.module.auth.service.AdminAccountProvisioner;

/** Transaction boundary for the initial ADMIN account provisioning use case. */
public class SpringTransactionalAdminAccountProvisioner implements AdminAccountProvisioner {

    private final AdminAccountProvisioner delegate;

    public SpringTransactionalAdminAccountProvisioner(AdminAccountProvisioner delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public boolean provision(String email, String rawPassword) {
        return delegate.provision(email, rawPassword);
    }
}
