package io.github.joannazadlo.recipedash.integration;

import io.github.joannazadlo.recipedash.integration.common.AbstractIntegrationTest;
import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.enums.UserOpinion;
import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeSummaryDto;
import io.github.joannazadlo.recipedash.repository.OpinionRepository;
import io.github.joannazadlo.recipedash.repository.RecipeRepository;
import io.github.joannazadlo.recipedash.repository.entity.Opinion;
import io.github.joannazadlo.recipedash.repository.entity.Recipe;
import io.github.joannazadlo.recipedash.repository.entity.RecipeIngredient;
import io.github.joannazadlo.recipedash.service.MealDbService;
import io.github.joannazadlo.recipedash.service.TastyService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class SearchIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private OpinionRepository opinionRepository;

    @MockitoBean
    private TastyService tastyService;

    @MockitoBean
    private MealDbService mealDbService;

    @AfterEach
    void cleanUp() {
        recipeRepository.deleteAll();
        opinionRepository.deleteAll();
    }

    @Test
    void searchRecipes_ShouldExcludeDislikedRecipes() throws Exception {
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

        String recipeId = savedRecipe.getId().toString();

        Opinion opinion = Opinion.builder()
                .user(user)
                .recipeId(recipeId)
                .recipeSource("User")
                .userOpinion(UserOpinion.DISLIKE)
                .build();

        opinionRepository.save(opinion);

        mockMvc.perform(get("/recipes/search")
                        .param("ingredients", "tomato")
                        .param("source", "User")
                        .param("excludeDisliked", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        assertEquals(1, recipeRepository.count(), "There should be exactly one recipe in db");
        assertEquals(1, opinionRepository.count(), "There should be exactly one opinion in db");
        assertEquals(UserOpinion.DISLIKE, opinionRepository.findAll().get(0).getUserOpinion());
    }

    @Test
    void searchRecipes_shouldReturnRecipeEvenIfDisliked_whenExcludeDislikedIsFalse() throws Exception {
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

        String recipeId = savedRecipe.getId().toString();

        Opinion opinion = Opinion.builder()
                .user(user)
                .recipeId(recipeId)
                .recipeSource("User")
                .userOpinion(UserOpinion.DISLIKE)
                .build();

        opinionRepository.save(opinion);

        mockMvc.perform(get("/recipes/search")
                        .param("ingredients", "tomato")
                        .param("source", "User")
                        .param("excludeDisliked", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(recipeId))
                .andExpect(jsonPath("$[0].title").value("Vegan Salad"))
                .andExpect(jsonPath("$[0].imageSource").value("/uploads/image.jpg"))
                .andExpect(jsonPath("$[0].source").value("User"));

        assertEquals(1, recipeRepository.count(), "There should be exactly one recipe in db");
        assertEquals(1, opinionRepository.count(), "There should be exactly one opinion in db");
        assertEquals(UserOpinion.DISLIKE, opinionRepository.findAll().get(0).getUserOpinion());
    }

    @Test
    void searchRecipes_ShouldExcludeDislikedTastyRecipes() throws Exception {
        ExternalRecipeSummaryDto tastyRecipe = ExternalRecipeSummaryDto.builder()
                .id("1")
                .title("Tasty meal")
                .imageSource("http://tasty/image.jpg")
                .build();

        when(tastyService.searchMeals(any())).thenReturn(List.of(tastyRecipe));

        Opinion opinion = Opinion.builder()
                .user(user)
                .recipeId("1")
                .recipeSource("Tasty")
                .userOpinion(UserOpinion.DISLIKE)
                .build();

        opinionRepository.save(opinion);

        mockMvc.perform(get("/recipes/search")
                        .param("ingredients", "tomato")
                        .param("source", "Tasty")
                        .param("excludeDisliked", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        assertEquals(1, opinionRepository.count(), "There should be exactly one opinion in db");
        assertEquals(UserOpinion.DISLIKE, opinionRepository.findAll().get(0).getUserOpinion());
    }

    @Test
    void searchRecipes_ShouldExcludeDislikedMealDbRecipes() throws Exception {
        ExternalRecipeSummaryDto mealDbRecipe = ExternalRecipeSummaryDto.builder()
                .id("1")
                .title("MealDb meal")
                .imageSource("http://mealdb/image.jpg")
                .build();

        when(mealDbService.searchMeals(any())).thenReturn(List.of(mealDbRecipe));

        Opinion opinion = Opinion.builder()
                .user(user)
                .recipeId("1")
                .recipeSource("MealDb")
                .userOpinion(UserOpinion.DISLIKE)
                .build();

        opinionRepository.save(opinion);

        mockMvc.perform(get("/recipes/search")
                        .param("ingredients", "tomato")
                        .param("source", "MealDb")
                        .param("excludeDisliked", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        assertEquals(1, opinionRepository.count(), "There should be exactly one opinion in db");
        assertEquals(UserOpinion.DISLIKE, opinionRepository.findAll().get(0).getUserOpinion());
    }

    @Test
    void searchRecipes_shouldExcludeOnlyDislikedRecipes() throws Exception {
        Recipe recipe1 = Recipe.builder()
                .title("Disliked Recipe")
                .imageSource("/uploads/dislikedImage.jpg")
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

        recipe1.getIngredients().forEach(ingredient -> ingredient.setRecipe(recipe1));

        Recipe savedRecipe = recipeRepository.save(recipe1);

        String recipeId = savedRecipe.getId().toString();

        Opinion opinion = Opinion.builder()
                .user(user)
                .recipeId(recipeId)
                .recipeSource("User")
                .userOpinion(UserOpinion.DISLIKE)
                .build();

        opinionRepository.save(opinion);

        Recipe recipe2 = Recipe.builder()
                .title("Liked Recipe")
                .imageSource("/uploads/likedImage.jpg")
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

        recipe2.getIngredients().forEach(ingredient -> ingredient.setRecipe(recipe2));

        Recipe savedRecipe2 = recipeRepository.save(recipe2);

        String recipeId2 = savedRecipe2.getId().toString();

        Opinion opinion2 = Opinion.builder()
                .user(user)
                .recipeId(recipeId2)
                .recipeSource("User")
                .userOpinion(UserOpinion.LIKE)
                .build();

        opinionRepository.save(opinion2);

        Recipe recipe3 = Recipe.builder()
                .title("Neutral Recipe")
                .imageSource("/uploads/neutralImage.jpg")
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

        recipe3.getIngredients().forEach(ingredient -> ingredient.setRecipe(recipe1));

        Recipe savedRecipe3 = recipeRepository.save(recipe3);

        String recipeId3 = savedRecipe3.getId().toString();

        Opinion opinion3 = Opinion.builder()
                .user(user)
                .recipeId(recipeId3)
                .recipeSource("User")
                .userOpinion(UserOpinion.NEUTRAL)
                .build();

        opinionRepository.save(opinion3);


        mockMvc.perform(get("/recipes/search")
                        .param("ingredients", "tomato")
                        .param("source", "User")
                        .param("excludeDisliked", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(recipeId2))
                .andExpect(jsonPath("$[0].title").value("Liked Recipe"))
                .andExpect(jsonPath("$[0].imageSource").value("/uploads/likedImage.jpg"))
                .andExpect(jsonPath("$[0].source").value("User"))
                .andExpect(jsonPath("$[1].id").value(recipeId3))
                .andExpect(jsonPath("$[1].title").value("Neutral Recipe"))
                .andExpect(jsonPath("$[1].imageSource").value("/uploads/neutralImage.jpg"))
                .andExpect(jsonPath("$[1].source").value("User"));

        assertEquals(3, recipeRepository.count(), "There should be 3 recipes in db");
        assertEquals(3, opinionRepository.count(), "There should be 3 opinions in db");
    }
}
