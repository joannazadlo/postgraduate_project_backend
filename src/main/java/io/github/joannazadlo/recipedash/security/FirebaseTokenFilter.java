package io.github.joannazadlo.recipedash.security;

import io.github.joannazadlo.recipedash.mapper.UserMapper;
import io.github.joannazadlo.recipedash.model.enums.Status;
import io.github.joannazadlo.recipedash.model.user.UserDto;
import io.github.joannazadlo.recipedash.repository.UserRepository;
import io.github.joannazadlo.recipedash.repository.entity.User;
import io.github.joannazadlo.recipedash.service.FirebaseTokenService;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private final FirebaseTokenService firebaseTokenService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/uploads/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )
            throws IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            reject(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Authorization token is missing or invalid");
            return;
        }

        String idToken = authorizationHeader.substring(7);

        try {
            FirebaseToken decodedToken = firebaseTokenService.verifyIdToken(idToken);
            String uid = decodedToken.getUid();

            User user = userRepository.findById(uid).orElse(null);

            if (user == null) {
                if ("POST".equals(request.getMethod()) && request.getRequestURI().equals("/users")) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(uid, null, List.of());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    request.setAttribute("uid", uid);
                    filterChain.doFilter(request, response);
                } else {
                    reject(response, HttpServletResponse.SC_UNAUTHORIZED,
                            "User not found");
                }
                return;
            }

            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );

            if (user.getStatus() == Status.BLOCKED) {
                reject(response, HttpServletResponse.SC_FORBIDDEN,
                        "User is blocked");
                return;
            }

            UserDto userDto = userMapper.toDto(user);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDto, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            reject(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired token");
        }
    }

    private void reject(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
