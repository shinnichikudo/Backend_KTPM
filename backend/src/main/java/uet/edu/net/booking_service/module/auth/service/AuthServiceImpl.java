package uet.edu.net.booking_service.module.auth.service;

import uet.edu.net.booking_service.core.exception.AppException;
import uet.edu.net.booking_service.core.exception.ErrorCode;
import uet.edu.net.booking_service.module.auth.contract.UserDTO;
import uet.edu.net.booking_service.module.auth.service.domain.AuthResult;
import uet.edu.net.booking_service.module.auth.service.domain.UserProfile;
import uet.edu.net.booking_service.module.auth.service.port.UserRepository;
import uet.edu.net.booking_service.module.auth.service.port.PasswordHasher;
import uet.edu.net.booking_service.module.auth.service.port.CredentialVerifier;
import uet.edu.net.booking_service.module.auth.service.port.AccessTokenIssuer;

import java.util.Locale;
import java.util.regex.Pattern;

public class AuthServiceImpl implements AuthService {

    private static final String CUSTOMER_ROLE = "CUSTOMER";
    private static final Pattern STRONG_PASSWORD = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).{8,}$");

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final CredentialVerifier credentialVerifier;
    private final AccessTokenIssuer accessTokenIssuer;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            CredentialVerifier credentialVerifier,
            AccessTokenIssuer accessTokenIssuer) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.credentialVerifier = credentialVerifier;
        this.accessTokenIssuer = accessTokenIssuer;
    }

    @Override
    public UserDTO register(String email, String rawPassword, String fullName, String phoneNumber) {
        String normalizedEmail = normalizeEmail(email);
        if (!STRONG_PASSWORD.matcher(rawPassword).matches()) {
            throw new AppException(ErrorCode.WEAK_PASSWORD);
        }
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        UserProfile newUser = new UserProfile(
                null,
                normalizedEmail,
                passwordHasher.hash(rawPassword),
                fullName.trim(),
                phoneNumber.trim(),
                CUSTOMER_ROLE,
                null,
                null);

        return toDto(userRepository.save(newUser));
    }

    @Override
    public AuthResult login(String email, String rawPassword) {
        String normalizedEmail = normalizeEmail(email);
        if (!credentialVerifier.hasValidCredentials(normalizedEmail, rawPassword)) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        UserProfile user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        return new AuthResult(accessTokenIssuer.issue(user.email(), user.role()), user.role());
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email))
                .map(AuthServiceImpl::toDto)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(AuthServiceImpl::toDto)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static UserDTO toDto(UserProfile user) {
        return new UserDTO(user.id(), user.email(), user.fullName(), user.role());
    }
}
