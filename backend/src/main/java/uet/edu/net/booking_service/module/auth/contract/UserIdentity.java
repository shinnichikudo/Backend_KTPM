package uet.edu.net.booking_service.module.auth.contract;

/** Minimal framework-independent identity shared with other application modules. */
public record UserIdentity(Long id, String email, String fullName, String role) {
}
