package uet.edu.net.booking_service.module.auth.api;

import org.springframework.stereotype.Component;

import uet.edu.net.booking_service.module.auth.api.dto.UserResponse;
import uet.edu.net.booking_service.module.auth.contract.UserIdentity;

/** Maps auth use case results to the HTTP representation. */
@Component
public class UserApiMapper {

    public UserResponse toResponse(UserIdentity identity) {
        return new UserResponse(identity.id(), identity.email(), identity.fullName(), identity.role());
    }
}
