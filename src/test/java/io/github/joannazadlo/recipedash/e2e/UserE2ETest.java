package io.github.joannazadlo.recipedash.e2e;

import io.github.joannazadlo.recipedash.config.MockFirebaseTokenServiceConfig;
import io.github.joannazadlo.recipedash.model.enums.Role;
import io.github.joannazadlo.recipedash.model.enums.Status;
import io.github.joannazadlo.recipedash.model.user.UserDto;
import io.github.joannazadlo.recipedash.model.user.UserSignUpDto;
import io.github.joannazadlo.recipedash.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(MockFirebaseTokenServiceConfig.class)
@ActiveProfiles("test-e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        restTemplate.getRestTemplate().getMessageConverters()
                .add(0, new MappingJackson2HttpMessageConverter());
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_shouldReturnAdminUserDto_WhenCreatingFirstUser() throws Exception {
        UserSignUpDto requestDto = UserSignUpDto.builder()
                .uid("user123")
                .email("test@test.com")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("faketoken");

        HttpEntity<UserSignUpDto> request = new HttpEntity<>(requestDto, headers);

        ResponseEntity<UserDto> response = restTemplate
                .postForEntity("/users", request, UserDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertEquals("user123", response.getBody().getUid());
        assertEquals("test@test.com", response.getBody().getEmail());
        Assertions.assertEquals(Role.ADMIN, response.getBody().getRole());
        Assertions.assertEquals(Status.ACTIVE, response.getBody().getStatus());
    }
}
