package uet.edu.net.booking_service.module.auth.service;

import uet.edu.net.booking_service.module.auth.contract.AuthServiceContract;
import uet.edu.net.booking_service.module.auth.contract.UserDTO;
import uet.edu.net.booking_service.module.auth.service.domain.AuthResult;

public interface AuthService extends AuthServiceContract {

    UserDTO register(String email, String rawPassword, String fullName, String phoneNumber);

    AuthResult login(String email, String rawPassword);

    UserDTO getUserByEmail(String email);
}
