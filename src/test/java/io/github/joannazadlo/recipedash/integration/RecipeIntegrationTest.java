package io.github.joannazadlo.recipedash.integration;

import io.github.joannazadlo.recipedash.integration.common.AbstractIntegrationTest;
import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.recipe.RecipeCreateDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeUpdateDto;
import io.github.joannazadlo.recipedash.model.recipeIngredient.RecipeIngredientCreateDto;
import io.github.joannazadlo.recipedash.model.recipeIngredient.RecipeIngredientUpdateDto;
import io.github.joannazadlo.recipedash.repository.RecipeRepository;
import io.github.joannazadlo.recipedash.repository.entity.Recipe;
import io.github.joannazadlo.recipedash.repository.entity.RecipeIngredient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class RecipeIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @AfterEach
    void cleanUp() {
        recipeRepository.deleteAll();
    }

    @Test
    void createRecipe_ShouldSaveRecipe() throws Exception {
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

        String responseContent = mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Vegan Salad"))
                .andExpect(jsonPath("$.ingredients.length()").value(2))
                .andExpect(jsonPath("$.ingredients[0].name").value("lettuce"))
                .andExpect(jsonPath("$.ingredients[0].quantity").value("100g"))
                .andExpect(jsonPath("$.ingredients[1].name").value("tomato"))
                .andExpect(jsonPath("$.ingredients[1].quantity").value("50g"))
                .andExpect(jsonPath("$.steps.length()").value(2))
                .andExpect(jsonPath("$.steps[0]").value("Chop vegetables"))
                .andExpect(jsonPath("$.steps[1]").value("Mix everything"))
                .andExpect(jsonPath("$.dietaryPreferences.length()").value(2))
                .andExpect(jsonPath("$.dietaryPreferences[0]").value("Vegan"))
                .andExpect(jsonPath("$.dietaryPreferences[1]").value("Gluten-Free"))
                .andExpect(jsonPath("$.cookingTime").value("15 minutes"))
                .andExpect(jsonPath("$.cuisine").value("European"))
                .andExpect(jsonPath("$.isPublic").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long recipeId = objectMapper.readTree(responseContent).get("id").asLong();

        Recipe recipe = fetchRecipeWithIngredientsAndStepsAndDietaryPreferences(recipeId);

        assertEquals("Vegan Salad", recipe.getTitle());
        assertEquals(2, recipe.getIngredients().size());
        assertEquals("lettuce", recipe.getIngredients().get(0).getName());
        assertEquals("100g", recipe.getIngredients().get(0).getQuantity());

        assertEquals("tomato", recipe.getIngredients().get(1).getName());
        assertEquals("50g", recipe.getIngredients().get(1).getQuantity());

        assertEquals(2, recipe.getSteps().size());
        assertEquals("Chop vegetables", recipe.getSteps().get(0));
        assertEquals("Mix everything", recipe.getSteps().get(1));

        assertEquals(List.of(DietaryPreferenceType.VEGAN, DietaryPreferenceType.GLUTEN_FREE), recipe.getDietaryPreferences());
        assertEquals("15 minutes", recipe.getCookingTime());
        assertEquals(CuisineType.EUROPEAN, recipe.getCuisine());
        assertTrue(recipe.isPublicRecipe());
    }

    @Test
    void updateRecipe_ShouldUpdateRecipe() throws Exception {
        Recipe recipe = Recipe.builder()
                .title("Vegan Salad")
                .imageSource("/uploads/image.jpg")
                .ingredients(List.of(
                        RecipeIngredient.builder().name("lettuce").quantity("100g").build(),
                        RecipeIngredient.builder().name("tomato").quantity("50g").build()
                ))
                .steps(List.of("Chop vegetables", "Mix everything"))
                .dietaryPreferences(List.of(DietaryPreferenceType.VEGAN, DietaryPreferenceType.GLUTEN_FREE))
                .cookingTime("15 minutes")
                .cuisine(CuisineType.EUROPEAN)
                .user(user)
                .publicRecipe(false)
                .build();

        recipe.getIngredients().forEach(ingredient -> ingredient.setRecipe(recipe));

        Recipe savedRecipe = recipeRepository.save(recipe);

        Long recipeId = savedRecipe.getId();

        assertEquals("Vegan Salad", savedRecipe.getTitle());
        assertEquals("lettuce", savedRecipe.getIngredients().get(0).getName());
        assertEquals("100g", savedRecipe.getIngredients().get(0).getQuantity());
        assertEquals("tomato", savedRecipe.getIngredients().get(1).getName());
        assertEquals("50g", savedRecipe.getIngredients().get(1).getQuantity());
        assertEquals(List.of(DietaryPreferenceType.VEGAN, DietaryPreferenceType.GLUTEN_FREE), savedRecipe.getDietaryPreferences());
        assertEquals("15 minutes", savedRecipe.getCookingTime());
        assertFalse(savedRecipe.isPublicRecipe());

        RecipeUpdateDto requestDto = RecipeUpdateDto.builder()
                .id(recipeId)
                .title("Vegan Salad Updated")
                .ingredients(List.of(
                        RecipeIngredientUpdateDto.builder().name("mixed lettuce").build(),
                        RecipeIngredientUpdateDto.builder().name("dry tomatoes").quantity("50g").build()
                ))
                .steps(List.of("Chop vegetables", "Mix everything"))
                .dietaryPreferences(List.of(DietaryPreferenceType.VEGAN, DietaryPreferenceType.VEGETARIAN))
                .cookingTime("30 minutes")
                .cuisine(CuisineType.EUROPEAN)
                .publicRecipe(true)
                .build();

        String responseContent = mockMvc.perform(put("/recipes/{id}", recipeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Vegan Salad Updated"))
                .andExpect(jsonPath("$.ingredients.length()").value(2))
                .andExpect(jsonPath("$.ingredients[0].name").value("mixed lettuce"))
                .andExpect(jsonPath("$.ingredients[0].quantity").value(nullValue()))
                .andExpect(jsonPath("$.ingredients[1].name").value("dry tomatoes"))
                .andExpect(jsonPath("$.ingredients[1].quantity").value("50g"))
                .andExpect(jsonPath("$.steps.length()").value(2))
                .andExpect(jsonPath("$.steps[0]").value("Chop vegetables"))
                .andExpect(jsonPath("$.steps[1]").value("Mix everything"))
                .andExpect(jsonPath("$.cookingTime").value("30 minutes"))
                .andExpect(jsonPath("$.dietaryPreferences.length()").value(2))
                .andExpect(jsonPath("$.dietaryPreferences[0]").value("Vegan"))
                .andExpect(jsonPath("$.dietaryPreferences[1]").value("Vegetarian"))
                .andExpect(jsonPath("$.cuisine").value("European"))
                .andExpect(jsonPath("$.isPublic").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Recipe updatedRecipe = fetchRecipeWithIngredientsAndStepsAndDietaryPreferences(recipeId);

        assertEquals("Vegan Salad Updated", updatedRecipe.getTitle());
        assertEquals("/uploads/image.jpg", updatedRecipe.getImageSource());
        assertEquals(2, updatedRecipe.getIngredients().size());
        assertEquals("mixed lettuce", updatedRecipe.getIngredients().get(0).getName());
        assertNull(updatedRecipe.getIngredients().get(0).getQuantity());

        assertEquals("dry tomatoes", updatedRecipe.getIngredients().get(1).getName());
        assertEquals("50g", updatedRecipe.getIngredients().get(1).getQuantity());

        assertEquals(2, updatedRecipe.getSteps().size());
        assertEquals("Chop vegetables", updatedRecipe.getSteps().get(0));
        assertEquals("Mix everything", updatedRecipe.getSteps().get(1));

        assertEquals(List.of(DietaryPreferenceType.VEGAN, DietaryPreferenceType.VEGETARIAN), updatedRecipe.getDietaryPreferences());
        assertEquals("30 minutes", updatedRecipe.getCookingTime());
        assertEquals(CuisineType.EUROPEAN, updatedRecipe.getCuisine());
        assertTrue(updatedRecipe.isPublicRecipe());
    }

    @Test
    void deleteRecipe_ShouldDeleteRecipe() throws Exception {
        Recipe recipe = Recipe.builder()
                .title("Vegan Salad")
                .imageSource("/uploads/image.jpg")
                .ingredients(List.of(
                        RecipeIngredient.builder().name("lettuce").quantity("100g").build(),
                        RecipeIngredient.builder().name("tomato").quantity("50g").build()
                ))
                .steps(List.of("Chop vegetables", "Mix everything"))
                .dietaryPreferences(List.of(DietaryPreferenceType.VEGAN, DietaryPreferenceType.GLUTEN_FREE))
                .cookingTime("15 minutes")
                .cuisine(CuisineType.EUROPEAN)
                .user(user)
                .publicRecipe(true)
                .build();

        recipe.getIngredients().forEach(ingredient -> ingredient.setRecipe(recipe));

        Recipe savedRecipe = recipeRepository.save(recipe);

        Long recipeId = savedRecipe.getId();

        mockMvc.perform(delete("/recipes/{id}", recipeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        boolean exists = recipeRepository.existsById(savedRecipe.getId());
        assertFalse(exists, "Recipe should be deleted from the database");

    }

    private Recipe fetchRecipeWithIngredientsAndStepsAndDietaryPreferences(Long id) {
        return transactionTemplate.execute(status -> {
            Recipe recipe = recipeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Recipe not found"));

            recipe.getIngredients().size();
            recipe.getSteps().size();
            recipe.getDietaryPreferences().size();

            return recipe;
        });
    }
}

