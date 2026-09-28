package uet.edu.net.booking_service.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter chạy MỘT LẦN mỗi request để kiểm tra JWT trong header Authorization.
 * Nếu token hợp lệ → set thông tin user vào SecurityContext để Spring biết ai đang gọi.
 *
 * Luồng xử lý:
 *   Request → JwtAuthFilter → [các filter khác] → Controller
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    // UserDetailsService load thông tin user từ DB theo email
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,   // @NonNull: Spring annotation, không phải jspecify
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Chỉ xử lý nếu header có dạng "Bearer <token>"
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // cắt bỏ "Bearer " lấy token thuần

            if (jwtUtils.isValid(token)) {
                String email = jwtUtils.extractEmail(token);

                // Load UserDetails từ DB để lấy authorities (roles)
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Tạo Authentication object và đặt vào SecurityContext
                // → Các Controller sau có thể dùng @AuthenticationPrincipal để lấy user
                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            // Token không hợp lệ → không set auth → request sẽ bị từ chối ở SecurityConfig
        }

        // Luôn gọi tiếp chain dù token có hay không
        filterChain.doFilter(request, response);
    }
}
