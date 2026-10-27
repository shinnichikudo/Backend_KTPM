package uet.edu.net.booking_service.module.auth.service;

import java.util.Locale;

import uet.edu.net.booking_service.module.auth.service.domain.UserProfile;
import uet.edu.net.booking_service.module.auth.service.port.PasswordHasher;
import uet.edu.net.booking_service.module.auth.service.port.UserRepository;

/** Framework-independent use case for creating the initial ADMIN account. */
public class AdminAccountProvisionerImpl implements AdminAccountProvisioner {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String ADMIN_NAME = "Admin";
    private static final String DEFAULT_PHONE_NUMBER = "0000000000";

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public AdminAccountProvisionerImpl(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public boolean provision(String email, String rawPassword) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(normalizedEmail)) {
            return false;
        }

        UserProfile admin = new UserProfile(
                null,
                normalizedEmail,
                passwordHasher.hash(rawPassword),
                ADMIN_NAME,
                DEFAULT_PHONE_NUMBER,
                ADMIN_ROLE,
                null,
                null);
        userRepository.save(admin);
        return true;
    }
}
