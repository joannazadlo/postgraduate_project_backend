package io.github.joannazadlo.recipedash.e2e;

import io.github.joannazadlo.recipedash.e2e.common.AbstractE2ETest;
import io.github.joannazadlo.recipedash.repository.RecipeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminRecipeE2ETest extends AbstractE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RecipeRepository recipeRepository;

    @AfterEach
    void cleanUp() {
        recipeRepository.deleteAll();
    }

    @Test
    void getAllRecipes_WhenUserIsNotAdmin_ReturnsForbidden() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("faketoken");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/admin/recipes",
                HttpMethod.GET,
                request,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
