package io.github.joannazadlo.recipedash.integration;

import io.github.joannazadlo.recipedash.model.enums.Role;
import io.github.joannazadlo.recipedash.model.enums.Status;
import io.github.joannazadlo.recipedash.model.user.UserSignUpDto;
import io.github.joannazadlo.recipedash.repository.UserRepository;
import io.github.joannazadlo.recipedash.repository.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        var uid = "user100";
        var auth = new UsernamePasswordAuthenticationToken(uid, null, List.of());
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
        userRepository.deleteAll();
    }

    @Test
    void createUser_ShouldCreateNewUser() throws Exception {
        UserSignUpDto requestDto = UserSignUpDto.builder()
                .uid("user100")
                .email("user100@gmail.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        Optional<User> user = userRepository.findById("user100");

        assertTrue(user.isPresent(), "User should be present");
        assertEquals("user100", user.get().getUid());
        assertEquals("user100@gmail.com", user.get().getEmail());
        assertEquals(Status.ACTIVE, user.get().getStatus());
        assertEquals(Role.ADMIN, user.get().getRole());
    }
}
