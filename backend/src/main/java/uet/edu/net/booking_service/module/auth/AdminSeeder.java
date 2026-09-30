package uet.edu.net.booking_service.module.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uet.edu.net.booking_service.module.auth.service.domain.UserProfile;
import uet.edu.net.booking_service.module.auth.service.port.UserRepository;

/** Seed tài khoản ADMIN khi có đủ hai biến môi trường cấu hình. */
@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${APP_ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${APP_ADMIN_PASSWORD:}")
    private String adminPassword;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        boolean emailBlank = adminEmail.isBlank();
        boolean passwordBlank = adminPassword.isBlank();
        if (emailBlank && passwordBlank) {
            return;
        }
        if (emailBlank || passwordBlank) {
            throw new IllegalStateException(
                    "AdminSeeder: phai cau hinh dong thoi APP_ADMIN_EMAIL va APP_ADMIN_PASSWORD.");
        }

        String normalizedEmail = adminEmail.trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            log.info("AdminSeeder: tai khoan {} da ton tai, bo qua.", normalizedEmail);
            return;
        }

        UserProfile admin = new UserProfile(
                null, normalizedEmail, passwordEncoder.encode(adminPassword),
                "Admin", "", "ADMIN", null, null);
        userRepository.save(admin);
        log.info("AdminSeeder: tai khoan ADMIN {} da duoc tao.", normalizedEmail);
    }
}
