package io.github.joannazadlo.recipedash.e2e;

import io.github.joannazadlo.recipedash.e2e.common.AbstractE2ETest;
import io.github.joannazadlo.recipedash.model.userIngredient.UserIngredientCreateDto;
import io.github.joannazadlo.recipedash.model.userIngredient.UserIngredientDto;
import io.github.joannazadlo.recipedash.repository.UserIngredientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserIngredientE2ETest extends AbstractE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserIngredientRepository userIngredientRepository;

    @AfterEach
    void cleanUp() {
        userIngredientRepository.deleteAll();
    }

    @Test
    void createUserIngredient_ShouldSaveUserIngredient() throws Exception {
        UserIngredientCreateDto requestDto = UserIngredientCreateDto.builder()
                .ingredient("Avocado")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("faketoken");

        HttpEntity<UserIngredientCreateDto> request = new HttpEntity<>(requestDto, headers);

        ResponseEntity<UserIngredientDto> response = restTemplate
                .postForEntity("/ingredients", request, UserIngredientDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Avocado", response.getBody().getIngredient());
    }
}
