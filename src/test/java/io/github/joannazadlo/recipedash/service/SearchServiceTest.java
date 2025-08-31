package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeSummaryDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeSummaryDto;
import io.github.joannazadlo.recipedash.model.recipe.SearchRecipeDto;
import io.github.joannazadlo.recipedash.model.searchCriteria.AllSourcesSearchCriteriaDto;
import io.github.joannazadlo.recipedash.model.searchCriteria.RecipeSearchCriteriaDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @Mock
    private TastyService tastyService;

    @Mock
    private MealDbService mealDbService;

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private SearchService searchService;

    @Test
    void searchRecipes_shouldCallAllSources_WhenSourceIsNull() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .ingredients(List.of("tomato"))
                .build();

        RecipeSearchCriteriaDto userCriteria = RecipeSearchCriteriaDto.builder()
                .ingredients(List.of("tomato"))
                .isPublic(true)
                .build();

        ExternalRecipeSummaryDto tastyRecipe = ExternalRecipeSummaryDto.builder()
                .id("1")
                .title("Tasty meal")
                .imageSource("http://tasty/image.jpg")
                .build();

        ExternalRecipeSummaryDto mealDbRecipe = ExternalRecipeSummaryDto.builder()
                .id("1")
                .title("MealDb meal")
                .imageSource("http://mealdb/image.jpg")
                .build();

        RecipeSummaryDto userRecipe = RecipeSummaryDto.builder()
                .id(1L)
                .title("User recipe")
                .imageSource("http://user/image.jpg")
                .build();

        when(tastyService.searchMeals(criteria)).thenReturn(List.of(tastyRecipe));
        when(mealDbService.searchMeals(criteria)).thenReturn(List.of(mealDbRecipe));
        when(recipeService.searchPublicRecipes(userCriteria)).thenReturn(List.of(userRecipe));

        List<SearchRecipeDto> recipes = searchService.searchRecipes(criteria);

        assertEquals(3, recipes.size());
    }

    @Test
    void searchRecipes_shouldCallOnlyTastyService_whenSourceIsTasty() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .ingredients(List.of("tomato"))
                .source("Tasty")
                .build();

        ExternalRecipeSummaryDto tastyRecipe = ExternalRecipeSummaryDto.builder()
                .id("1")
                .title("Tasty meal")
                .imageSource("http://tasty/image.jpg")
                .build();

        when(tastyService.searchMeals(criteria)).thenReturn(List.of(tastyRecipe));

        List<SearchRecipeDto> recipes = searchService.searchRecipes(criteria);

        assertEquals(1, recipes.size());
        assertEquals("Tasty", recipes.get(0).getSource());

        verify(tastyService).searchMeals(any());
        verifyNoInteractions(mealDbService, recipeService);
    }
}
