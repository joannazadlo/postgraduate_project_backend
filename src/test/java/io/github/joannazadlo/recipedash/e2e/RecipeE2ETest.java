package io.github.joannazadlo.recipedash.e2e;

import io.github.joannazadlo.recipedash.e2e.common.AbstractE2ETest;
import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.recipe.RecipeCreateDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeDetailsDto;
import io.github.joannazadlo.recipedash.model.recipeIngredient.RecipeIngredientCreateDto;
import io.github.joannazadlo.recipedash.model.recipeIngredient.RecipeIngredientDto;
import io.github.joannazadlo.recipedash.repository.RecipeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecipeE2ETest extends AbstractE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RecipeRepository recipeRepository;

    @AfterEach
    void cleanUp() {
        recipeRepository.deleteAll();
    }

    @Test
    void createRecipe_WithValidData_ReturnsRecipeDetails() throws Exception {
        RecipeCreateDto requestDto = RecipeCreateDto.builder()
                .title("Vegan Salad")
                .ingredients(List.of(
                        RecipeIngredientCreateDto.builder().name("lettuce").quantity("100g").build(),
                        RecipeIngredientCreateDto.builder().name("tomato").quantity("50g").build()
                ))
                .steps(List.of("Chop vegetables", "Mix everything"))
                .dietaryPreferences(List.of(DietaryPreferenceType.VEGAN, DietaryPreferenceType.GLUTEN_FREE))
                .cookingTime("15 minutes")
                .cuisine(CuisineType.EUROPEAN)
                .publicRecipe(true)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("faketoken");

        HttpEntity<RecipeCreateDto> request = new HttpEntity<>(requestDto, headers);

        ResponseEntity<RecipeDetailsDto> response = restTemplate
                .postForEntity("/recipes", request, RecipeDetailsDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Vegan Salad", response.getBody().getTitle());

        List<RecipeIngredientDto> ingredients = response.getBody().getIngredients();
        assertEquals(2, ingredients.size());
        assertTrue(ingredients.stream().anyMatch(i -> i.getName().equals("lettuce") && i.getQuantity().equals("100g")));
        assertTrue(ingredients.stream().anyMatch(i -> i.getName().equals("tomato") && i.getQuantity().equals("50g")));

        List<String> steps = response.getBody().getSteps();

        assertEquals(2, steps.size());
        assertEquals("Chop vegetables", steps.get(0));
        assertEquals("Mix everything", steps.get(1));

        assertEquals("15 minutes", response.getBody().getCookingTime());
        assertTrue(response.getBody().getDietaryPreferences().contains(DietaryPreferenceType.VEGAN));
        assertTrue(response.getBody().getDietaryPreferences().contains(DietaryPreferenceType.GLUTEN_FREE));
        Assertions.assertEquals(CuisineType.EUROPEAN, request.getBody().getCuisine());
        assertTrue(response.getBody().isPublicRecipe());
    }
}
