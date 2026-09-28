package uet.edu.net.booking_service.module.auth.service.port;

import uet.edu.net.booking_service.module.auth.service.domain.UserProfile;

import java.util.Optional;

/** Persistence port required by auth use cases. */
public interface UserRepository {

    Optional<UserProfile> findByEmail(String email);

    Optional<UserProfile> findById(Long userId);

    boolean existsByEmail(String email);

    UserProfile save(UserProfile user);
}
