package uet.edu.net.booking_service.module.auth.persistence;

import org.springframework.stereotype.Repository;

import uet.edu.net.booking_service.module.auth.service.domain.UserProfile;
import uet.edu.net.booking_service.module.auth.service.port.UserRepository;

import java.util.Optional;

@Repository
public class UserRepositoryJpaAdapter implements UserRepository {

    private final UserRepositoryJpa jpaRepository;

    public UserRepositoryJpaAdapter(UserRepositoryJpa jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<UserProfile> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    public Optional<UserProfile> findById(Long userId) {
        return jpaRepository.findById(userId).map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public UserProfile save(UserProfile user) {
        UserEntity saved = jpaRepository.save(UserMapper.toEntity(user));
        return UserMapper.toDomain(saved);
    }
}
