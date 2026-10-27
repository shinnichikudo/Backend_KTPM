package uet.edu.net.booking_service.module.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import uet.edu.net.booking_service.module.auth.service.UserService;
import uet.edu.net.booking_service.module.auth.service.UserServiceImpl;
import uet.edu.net.booking_service.module.auth.service.AuthService;
import uet.edu.net.booking_service.module.auth.service.AuthServiceImpl;
import uet.edu.net.booking_service.module.auth.service.port.AccessTokenIssuer;
import uet.edu.net.booking_service.module.auth.service.port.CredentialVerifier;
import uet.edu.net.booking_service.module.auth.service.port.PasswordHasher;
import uet.edu.net.booking_service.module.auth.service.port.UserRepository;

@Configuration
public class AuthServiceConfiguration {

    @Bean
    UserServiceImpl userServiceImpl(UserRepository userRepository, PasswordHasher passwordHasher) {
        return new UserServiceImpl(userRepository, passwordHasher);
    }

    @Bean
    @Primary
    UserService userService(UserServiceImpl userService) {
        return new SpringTransactionalUserService(userService);
    }

    @Bean
    AuthServiceImpl authServiceImpl(UserRepository userRepository, PasswordHasher passwordHasher,
            CredentialVerifier credentialVerifier, AccessTokenIssuer accessTokenIssuer) {
        return new AuthServiceImpl(userRepository, passwordHasher, credentialVerifier, accessTokenIssuer);
    }

    @Bean
    @Primary
    AuthService authService(AuthServiceImpl authService) {
        return new SpringTransactionalAuthService(authService);
    }
}
