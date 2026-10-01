package uet.edu.net.booking_service.module.auth.api;

import jakarta.validation.Valid;
import uet.edu.net.booking_service.module.auth.api.dto.LoginRequest;
import uet.edu.net.booking_service.module.auth.api.dto.LoginResponse;
import uet.edu.net.booking_service.module.auth.api.dto.RegisterRequest;
import uet.edu.net.booking_service.module.auth.contract.UserDTO;
import uet.edu.net.booking_service.module.auth.service.AuthService;
import uet.edu.net.booking_service.module.auth.service.domain.AuthResult;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

@RestController
@RequestMapping("/api/auth")
@SecurityRequirements
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        UserDTO user = authService.register(
                request.email(), request.password(), request.fullName(), request.phoneNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = authService.login(request.email(), request.password());
        return new LoginResponse(result.accessToken(), "Bearer", result.role());
    }
}
