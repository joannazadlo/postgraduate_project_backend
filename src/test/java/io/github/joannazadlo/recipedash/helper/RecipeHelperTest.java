package io.github.joannazadlo.recipedash.helper;

import io.github.joannazadlo.recipedash.mapper.IngredientMapper;
import io.github.joannazadlo.recipedash.mapper.RecipeMapper;
import io.github.joannazadlo.recipedash.mapper.UserMapper;
import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.recipe.RecipeCreateDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeSummaryWithUserDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeUpdateDto;
import io.github.joannazadlo.recipedash.model.recipeIngredient.RecipeIngredientCreateDto;
import io.github.joannazadlo.recipedash.model.recipeIngredient.RecipeIngredientUpdateDto;
import io.github.joannazadlo.recipedash.model.user.UserDto;
import io.github.joannazadlo.recipedash.repository.entity.Recipe;
import io.github.joannazadlo.recipedash.repository.entity.RecipeIngredient;
import io.github.joannazadlo.recipedash.repository.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecipeHelperTest {

    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private IngredientMapper ingredientMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private RecipeHelper recipeHelper;

    @Test
    void prepareRecipeEntity_shouldSetFieldsCorrectly() {
        RecipeCreateDto dto = RecipeCreateDto.builder()
                .title("Test")
                .ingredients(List.of(
                        RecipeIngredientCreateDto.builder().name("Tomato").build()
                ))
                .build();

        Recipe mapped = Recipe.builder()
                .title("Test")
                .ingredients(List.of(RecipeIngredient.builder().name("Tomato").build()))
                .build();

        User user = User.builder().email("test@example.com").build();

        when(recipeMapper.toEntity(dto)).thenReturn(mapped);

        Recipe result = recipeHelper.prepareRecipeEntity(dto, user, "img.png");

        assertEquals(user, result.getUser());
        assertEquals("img.png", result.getImageSource());
        assertEquals(result, result.getIngredients().get(0).getRecipe());
    }

    @Test
    void updateRecipeFieldsFromDto_shouldUpdateFieldsProperly() {
        RecipeUpdateDto dto = RecipeUpdateDto.builder()
                .title("Updated Title")
                .steps(List.of("step1", "step2"))
                .ingredients(List.of(RecipeIngredientUpdateDto.builder().id(1L).name("Rice").build()))
                .publicRecipe(true)
                .cuisine(CuisineType.NORTH_AMERICAN)
                .cookingTime("30 minutes")
                .dietaryPreferences(List.of(DietaryPreferenceType.VEGAN))
                .build();

        RecipeIngredient updatedIng = RecipeIngredient.builder().name("Rice").build();
        when(ingredientMapper.toUpdateEntity(dto.getIngredients().get(0))).thenReturn(updatedIng);

        Recipe existing = Recipe.builder()
                .title("Old")
                .steps(new ArrayList<>(List.of("oldStep")))
                .ingredients(new ArrayList<>(List.of(RecipeIngredient.builder().name("Old").build())))
                .dietaryPreferences(new ArrayList<>(List.of(DietaryPreferenceType.GLUTEN_FREE)))
                .build();

        recipeHelper.updateRecipeFieldsFromDto(dto, existing);

        assertEquals("Updated Title", existing.getTitle());
        assertEquals(List.of("step1", "step2"), existing.getSteps());
        assertEquals(1, existing.getIngredients().size());
        assertEquals("Rice", existing.getIngredients().get(0).getName());
        assertEquals(existing, existing.getIngredients().get(0).getRecipe());
        assertEquals("30 minutes", existing.getCookingTime());
        assertEquals(List.of(DietaryPreferenceType.VEGAN), existing.getDietaryPreferences());
    }

    @Test
    void recipeMatchesSearchCriteria_shouldMatchCorrectly() {
        Recipe recipe = Recipe.builder()
                .ingredients(List.of(
                        RecipeIngredient.builder().name("Tomato").build(),
                        RecipeIngredient.builder().name("Onion").build()
                ))
                .cuisine(CuisineType.EUROPEAN)
                .dietaryPreferences(List.of(DietaryPreferenceType.VEGAN))
                .build();

        Predicate<Recipe> predicate = recipeHelper.recipeMatchesSearchCriteria(
                List.of("Tomato"), CuisineType.EUROPEAN, List.of(DietaryPreferenceType.VEGAN)
        );

        assertTrue(predicate.test(recipe));
    }

    @Test
    void recipeMatchesSearchCriteria_shouldRejectWrongCuisine() {
        Recipe recipe = Recipe.builder()
                .ingredients(List.of(RecipeIngredient.builder().name("Tomato").build()))
                .cuisine(CuisineType.ASIAN)
                .dietaryPreferences(List.of(DietaryPreferenceType.VEGAN))
                .build();

        Predicate<Recipe> predicate = recipeHelper.recipeMatchesSearchCriteria(
                List.of("Tomato"),
                CuisineType.MIDDLE_EASTERN,
                List.of(DietaryPreferenceType.VEGAN)
        );

        assertFalse(predicate.test(recipe));
    }

    @Test
    void filterByPublicStatus_shouldMatchCorrectly() {
        Recipe publicRecipe = Recipe.builder().publicRecipe(true).build();
        Recipe privateRecipe = Recipe.builder().publicRecipe(false).build();

        assertTrue(recipeHelper.filterByPublicStatus(null).test(publicRecipe));
        assertTrue(recipeHelper.filterByPublicStatus(true).test(publicRecipe));
        assertFalse(recipeHelper.filterByPublicStatus(true).test(privateRecipe));
    }

    @Test
    void mapRecipeWithUserDto_shouldMapFieldsProperly() {
        Recipe recipe = Recipe.builder()
                .id(123L)
                .title("Recipe")
                .imageSource("img.jpg")
                .createdAt(LocalDateTime.now())
                .publicRecipe(true)
                .build();

        User user = User.builder().email("user@example.com").build();
        UserDto userDto = UserDto.builder().email("user@example.com").build();

        when(userMapper.toDto(user)).thenReturn(userDto);

        RecipeSummaryWithUserDto result = recipeHelper.mapRecipeWithUserDto(recipe, user);

        assertEquals(recipe.getId(), result.getId());
        assertEquals(recipe.getTitle(), result.getTitle());
        assertEquals("user@example.com", result.getUserEmail());
        assertEquals(recipe.getImageSource(), result.getImageSource());
    }
}
