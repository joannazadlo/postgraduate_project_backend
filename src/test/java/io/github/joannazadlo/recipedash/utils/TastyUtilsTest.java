package io.github.joannazadlo.recipedash.utils;

import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.searchCriteria.AllSourcesSearchCriteriaDto;
import io.github.joannazadlo.recipedash.model.tasty.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TastyUtilsTest {

    @Test
    void buildSearchQuery_shouldReturnIngredient_WhenIngredientsExist() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .ingredients(List.of("Tomato", "Chicken"))
                .build();

        String query = TastyUtils.buildSearchQuery(criteria);

        assertEquals("Tomato", query);
    }

    @Test
    void buildSearchQuery_shouldReturnCuisine_WhenNoIngredients() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .cuisine(CuisineType.AFRICAN)
                .build();

        String query = TastyUtils.buildSearchQuery(criteria);

        assertEquals("african", query);
    }

    @Test
    void buildSearchQuery_shouldReturnDietaryPreferences_WhenNoIngredientsAndCuisine() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .dietaryPreferences(List.of(DietaryPreferenceType.GLUTEN_FREE))
                .build();

        String query = TastyUtils.buildSearchQuery(criteria);

        assertEquals("gluten_free", query);
    }

    @Test
    void extractIngredientsShouldReturnLowerCaseIngredients() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .ingredients(List.of("Tomato", "Chicken"))
                .build();

        List<String> ingredients = TastyUtils.extractRequiredIngredients(criteria);

        assertEquals(List.of("tomato", "chicken"), ingredients);
    }

    @Test
    void filterByCuisine_ShouldReturnTrue_WhenCuisineMatches() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .cuisine(CuisineType.AFRICAN)
                .build();

        TastyRecipeRaw recipe = TastyRecipeRaw.builder()
                .tags(List.of(new TastyTag("African", "cuisine")))
                .build();

        assertTrue(TastyUtils.filterByCuisine(criteria, recipe));
    }

    @Test
    void filterByCuisine_ShouldReturnFalse_WhenCuisineDoesNotMatch() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .cuisine(CuisineType.EUROPEAN)
                .build();

        TastyRecipeRaw recipe = TastyRecipeRaw.builder()
                .tags(List.of(new TastyTag("African", "cuisine")))
                .build();

        assertFalse(TastyUtils.filterByCuisine(criteria, recipe));
    }

    @Test
    void filterByCuisine_ShouldReturnTrue_WhenNoCuisineInSearchCriteria() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder().build();

        TastyRecipeRaw recipe = TastyRecipeRaw.builder().build();

        assertTrue(TastyUtils.filterByCuisine(criteria, recipe));
    }

    @Test
    void filterByIngredients_ShouldReturnTrue_WhenAllIngredientsPresent() {
        List<String> required = List.of("tomato", "chicken");

        TastyRecipeRaw recipe = TastyRecipeRaw.builder()
                .sections(List.of(
                        Section.builder()
                                .components(List.of(
                                        Component.builder()
                                                .ingredient(Ingredient.builder().name("Tomato").build())
                                                .build(),
                                        Component.builder()
                                                .ingredient(Ingredient.builder().name("Chicken").build())
                                                .build()
                                ))
                                .build()
                ))
                .build();

        assertTrue(TastyUtils.filterByIngredients(recipe, required));
    }

    @Test
    void filterByIngredients_ShouldReturnFalse_WhenIngredientMissing() {
        List<String> required = List.of("tomato", "chicken");

        TastyRecipeRaw recipe = TastyRecipeRaw.builder()
                .sections(List.of(
                        Section.builder()
                                .components(List.of(
                                        Component.builder()
                                                .ingredient(Ingredient.builder().name("Tomato").build())
                                                .build(),
                                        Component.builder()
                                                .ingredient(Ingredient.builder().name("Onion").build())
                                                .build()
                                ))
                                .build()
                ))
                .build();

        assertFalse(TastyUtils.filterByIngredients(recipe, required));
    }

    @Test
    void filterByDietaryPreferences_ShouldReturnTrue_WhenDietaryPreferenceMatches() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .dietaryPreferences(List.of(DietaryPreferenceType.VEGAN))
                .build();

        TastyRecipeRaw recipe = TastyRecipeRaw.builder()
                .tags(List.of(new TastyTag("Vegan", "dietary")))
                .build();

        assertTrue(TastyUtils.filterByCuisine(criteria, recipe));
    }

    @Test
    void filterByDietaryPreferences_ShouldReturnFalse_WhenDietaryPreferenceDoesNotMatch() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .dietaryPreferences(List.of(DietaryPreferenceType.GLUTEN_FREE))
                .build();

        TastyRecipeRaw recipe = TastyRecipeRaw.builder()
                .tags(List.of(new TastyTag("Vegan", "dietary")))
                .build();

        assertFalse(TastyUtils.filterByDietaryPreferences(criteria, recipe));
    }

    @Test
    void filterByIngredients_ShouldReturnTrue_WhenNoRequiredIngredients() {
        TastyRecipeRaw recipe = TastyRecipeRaw.builder().build();

        assertTrue(TastyUtils.filterByIngredients(recipe, List.of()));
    }

    @Test
    void filterByDietaryPreferences_ShouldReturnTrue_WhenNoDietaryPreferencesInSearchCriteria() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder().build();

        TastyRecipeRaw recipe = TastyRecipeRaw.builder().build();

        assertTrue(TastyUtils.filterByDietaryPreferences(criteria, recipe));
    }
}
