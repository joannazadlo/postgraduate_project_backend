package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.mapper.MealDbMapper;
import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeSummaryDto;
import io.github.joannazadlo.recipedash.model.mealdb.MealDbSummary;
import io.github.joannazadlo.recipedash.model.mealdb.MealDbSummaryResponse;
import io.github.joannazadlo.recipedash.model.searchCriteria.AllSourcesSearchCriteriaDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MealDbServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MealDbMapper mealDbMapper;

    @InjectMocks
    private MealDbService mealDbService;

    @Test
    void searchMeals_shouldReturnMappedResults() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .ingredients(List.of("tomato"))
                .cuisine(CuisineType.AFRICAN)
                .dietaryPreferences(List.of(DietaryPreferenceType.VEGETARIAN))
                .build();

        MealDbSummary africanMeal1 = MealDbSummary.builder()
                .idMeal("1")
                .strMeal("African Vegetarian Meal with Tomato")
                .strMealThumb("url1")
                .build();

        MealDbSummary africanMeal2 = MealDbSummary.builder()
                .idMeal("2")
                .strMeal("African Non-Vegetarian Meal with Tomato")
                .strMealThumb("url2")
                .build();

        MealDbSummary africanMeal3 = MealDbSummary.builder()
                .idMeal("3")
                .strMeal("African Vegetarian Meal without Tomato")
                .strMealThumb("url3")
                .build();

        MealDbSummaryResponse ingredientResponse = new MealDbSummaryResponse();
        ingredientResponse.setMeals(List.of(africanMeal1, africanMeal2));

        MealDbSummaryResponse cuisineResponse = new MealDbSummaryResponse();
        cuisineResponse.setMeals(List.of(africanMeal1, africanMeal2, africanMeal3));

        MealDbSummaryResponse dietResponse = new MealDbSummaryResponse();
        dietResponse.setMeals(List.of(africanMeal1, africanMeal3));

        when(restTemplate.getForObject(contains("filter.php?i=tomato"), eq(MealDbSummaryResponse.class)))
                .thenReturn(ingredientResponse);

        when(restTemplate.getForObject(contains("filter.php?a=Egyptian"), eq(MealDbSummaryResponse.class)))
                .thenReturn(cuisineResponse);

        when(restTemplate.getForObject(contains("filter.php?a=Kenyan"), eq(MealDbSummaryResponse.class)))
                .thenReturn(new MealDbSummaryResponse(List.of()));

        when(restTemplate.getForObject(contains("filter.php?a=Moroccan"), eq(MealDbSummaryResponse.class)))
                .thenReturn(new MealDbSummaryResponse(List.of()));

        when(restTemplate.getForObject(contains("filter.php?a=Tunisian"), eq(MealDbSummaryResponse.class)))
                .thenReturn(new MealDbSummaryResponse(List.of()));

        when(restTemplate.getForObject(contains("filter.php?c=Vegetarian"), eq(MealDbSummaryResponse.class)))
                .thenReturn(dietResponse);

        when(mealDbMapper.mapMealDbSummaryToRecipeSummaryDto(any(MealDbSummary.class))).thenAnswer(invocation -> {
            MealDbSummary summary = invocation.getArgument(0);
            return ExternalRecipeSummaryDto.builder()
                    .id(summary.getIdMeal())
                    .title(summary.getStrMeal())
                    .imageSource(summary.getStrMealThumb())
                    .build();
        });

        List<ExternalRecipeSummaryDto> results = mealDbService.searchMeals(criteria);

        assertEquals(1, results.size());
        assertEquals("1", results.get(0).getId());
        assertEquals("African Vegetarian Meal with Tomato", results.get(0).getTitle());

        verify(restTemplate, atLeastOnce()).getForObject(anyString(), eq(MealDbSummaryResponse.class));
        verify(mealDbMapper, atLeastOnce()).mapMealDbSummaryToRecipeSummaryDto(any());
    }

    @Test
    void searchMeals_shouldReturnMappedResults_WhenSearchingByIngredientsOnly() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .ingredients(List.of("chicken"))
                .build();

        MealDbSummary firstMeal = MealDbSummary.builder()
                .idMeal("1")
                .strMeal("Chicken with rice")
                .strMealThumb("url1")
                .build();

        MealDbSummary secondMeal = MealDbSummary.builder()
                .idMeal("2")
                .strMeal("Chicken")
                .strMealThumb("url2")
                .build();

        MealDbSummaryResponse ingredientResponse = new MealDbSummaryResponse();
        ingredientResponse.setMeals(List.of(firstMeal, secondMeal));

        when(restTemplate.getForObject(contains("filter.php?i=chicken"), eq(MealDbSummaryResponse.class)))
                .thenReturn(ingredientResponse);

        when(mealDbMapper.mapMealDbSummaryToRecipeSummaryDto(any(MealDbSummary.class))).thenAnswer(invocation -> {
            MealDbSummary summary = invocation.getArgument(0);
            return ExternalRecipeSummaryDto.builder()
                    .id(summary.getIdMeal())
                    .title(summary.getStrMeal())
                    .imageSource(summary.getStrMealThumb())
                    .build();
        });

        List<ExternalRecipeSummaryDto> results = mealDbService.searchMeals(criteria);

        assertEquals(2, results.size());

        assertEquals("1", results.get(0).getId());
        assertEquals("Chicken with rice", results.get(0).getTitle());

        assertEquals("2", results.get(1).getId());
        assertEquals("Chicken", results.get(1).getTitle());

        verify(restTemplate, atLeastOnce()).getForObject(contains("filter.php?i=chicken"), eq(MealDbSummaryResponse.class));
        verify(mealDbMapper, times(2)).mapMealDbSummaryToRecipeSummaryDto(any(MealDbSummary.class));
        verify(restTemplate, never()).getForObject(contains("filter.php?a="), eq(MealDbSummaryResponse.class));
        verify(restTemplate, never()).getForObject(contains("filter.php?c="), eq(MealDbSummaryResponse.class));
    }

    @Test
    void searchMeals_shouldReturnMappedResults_WhenSearchingByVeganAndVegetarian() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .dietaryPreferences(List.of(DietaryPreferenceType.VEGAN, DietaryPreferenceType.VEGETARIAN))
                .build();

        MealDbSummary firstMeal = MealDbSummary.builder()
                .idMeal("1")
                .strMeal("Vegan Salad")
                .strMealThumb("url1")
                .build();

        MealDbSummary secondMeal = MealDbSummary.builder()
                .idMeal("2")
                .strMeal("Vegan Lasagne")
                .strMealThumb("url2")
                .build();

        MealDbSummaryResponse dietResponse = new MealDbSummaryResponse();
        dietResponse.setMeals(List.of(firstMeal, secondMeal));

        when(restTemplate.getForObject(contains("filter.php?c=Vegan"), eq(MealDbSummaryResponse.class)))
                .thenReturn(dietResponse);

        when(mealDbMapper.mapMealDbSummaryToRecipeSummaryDto(any(MealDbSummary.class))).thenAnswer(invocation -> {
            MealDbSummary summary = invocation.getArgument(0);
            return ExternalRecipeSummaryDto.builder()
                    .id(summary.getIdMeal())
                    .title(summary.getStrMeal())
                    .imageSource(summary.getStrMealThumb())
                    .build();
        });

        List<ExternalRecipeSummaryDto> results = mealDbService.searchMeals(criteria);

        assertEquals(2, results.size());

        assertEquals("1", results.get(0).getId());
        assertEquals("Vegan Salad", results.get(0).getTitle());

        assertEquals("2", results.get(1).getId());
        assertEquals("Vegan Lasagne", results.get(1).getTitle());

        verify(restTemplate, atLeastOnce()).getForObject(contains("filter.php?c=Vegan"), eq(MealDbSummaryResponse.class));
        verify(mealDbMapper, times(2)).mapMealDbSummaryToRecipeSummaryDto(any(MealDbSummary.class));
    }

    @Test
    void searchMeals_shouldReturnEmptyList_whenApiReturnsEmptyMealsList() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .ingredients(List.of("nonexistent"))
                .build();

        when(restTemplate.getForObject(contains("filter.php?i=nonexistent"), eq(MealDbSummaryResponse.class)))
                .thenReturn(new MealDbSummaryResponse(List.of()));

        List<ExternalRecipeSummaryDto> results = mealDbService.searchMeals(criteria);
        assertTrue(results.isEmpty());
    }
}
