package uet.edu.net.booking_service.module.auth.service;

/** Provisions the configured administrative account when it is absent. */
public interface AdminAccountProvisioner {
    boolean provision(String email, String rawPassword);
}
