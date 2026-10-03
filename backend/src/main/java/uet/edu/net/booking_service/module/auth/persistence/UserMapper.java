package uet.edu.net.booking_service.module.auth.persistence;

import uet.edu.net.booking_service.module.auth.service.domain.UserProfile;

final class UserMapper {

    private UserMapper() {
    }

    static UserProfile toDomain(UserEntity entity) {
        return new UserProfile(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFullName(),
                entity.getPhoneNumber(),
                entity.getRole(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    static UserEntity toEntity(UserProfile profile) {
        UserEntity entity = new UserEntity();
        entity.setId(profile.id());
        entity.setEmail(profile.email());
        entity.setPasswordHash(profile.passwordHash());
        entity.setFullName(profile.fullName());
        entity.setPhoneNumber(profile.phoneNumber());
        entity.setRole(profile.role());
        entity.setCreatedAt(profile.createdAt());
        entity.setUpdatedAt(profile.updatedAt());
        return entity;
    }
}
