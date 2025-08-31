package io.github.joannazadlo.recipedash.e2e.common;

import io.github.joannazadlo.recipedash.config.MockFirebaseTokenServiceConfig;
import io.github.joannazadlo.recipedash.model.enums.Role;
import io.github.joannazadlo.recipedash.model.enums.Status;
import io.github.joannazadlo.recipedash.repository.UserRepository;
import io.github.joannazadlo.recipedash.repository.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(MockFirebaseTokenServiceConfig.class)
@ActiveProfiles("test-e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AbstractE2ETest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = User.builder()
                .uid("user123")
                .email("test@test.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        userRepository.save(user);
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }
}
