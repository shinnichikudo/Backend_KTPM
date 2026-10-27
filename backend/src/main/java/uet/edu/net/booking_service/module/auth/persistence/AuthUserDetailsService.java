package uet.edu.net.booking_service.module.auth.persistence;

import java.util.Locale;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import uet.edu.net.booking_service.module.auth.service.domain.UserProfile;
import uet.edu.net.booking_service.module.auth.service.port.UserRepository;

@Service
public class AuthUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public AuthUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        UserProfile profile = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(profile.email())
                .password(profile.passwordHash())
                .authorities(new SimpleGrantedAuthority("ROLE_" + profile.role()))
                .build();
    }
}
