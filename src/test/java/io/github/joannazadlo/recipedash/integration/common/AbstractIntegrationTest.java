package io.github.joannazadlo.recipedash.integration.common;

import io.github.joannazadlo.recipedash.model.enums.Role;
import io.github.joannazadlo.recipedash.model.enums.Status;
import io.github.joannazadlo.recipedash.model.user.UserDto;
import io.github.joannazadlo.recipedash.repository.UserRepository;
import io.github.joannazadlo.recipedash.repository.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
public class AbstractIntegrationTest {

    @Autowired
    protected UserRepository userRepository;

    protected User user;

    @BeforeEach
    void setUpSecurityContext() {
        user = User.builder()
                .uid("user123")
                .email("test@test.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        userRepository.save(user);

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserDto userDto = UserDto.builder()
                .uid("user123")
                .email("test@test.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDto, null, authorities);

        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
        userRepository.deleteAll();
    }
}
