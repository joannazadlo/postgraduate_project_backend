package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.exception.mealDb.MealDbServiceUnavailableException;
import io.github.joannazadlo.recipedash.exception.mealDb.MealNotFoundException;
import io.github.joannazadlo.recipedash.mapper.MealDbMapper;
import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeDetailsDto;
import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeSummaryDto;
import io.github.joannazadlo.recipedash.model.mealdb.MealDbDetails;
import io.github.joannazadlo.recipedash.model.mealdb.MealDbDetailsResponse;
import io.github.joannazadlo.recipedash.model.mealdb.MealDbSummary;
import io.github.joannazadlo.recipedash.model.mealdb.MealDbSummaryResponse;
import io.github.joannazadlo.recipedash.model.searchCriteria.AllSourcesSearchCriteriaDto;
import io.github.joannazadlo.recipedash.utils.MealDbUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static io.github.joannazadlo.recipedash.constants.MealDbApiEndpoints.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MealDbService {

    private final RestTemplate restTemplate;
    private final MealDbMapper mealDbMapper;

    public List<ExternalRecipeSummaryDto> searchMeals(AllSourcesSearchCriteriaDto criteria) {
        Set<MealDbSummary> mealSummaries = searchMealSummaries(criteria);

        return mealSummaries.stream()
                .map(mealDbMapper::mapMealDbSummaryToRecipeSummaryDto)
                .collect(Collectors.toList());
    }

    private Set<MealDbSummary> searchMealSummaries(AllSourcesSearchCriteriaDto criteria) {
        List<Set<MealDbSummary>> mealSets = new ArrayList<>();

        CuisineType cuisine = criteria.getCuisine();
        List<DietaryPreferenceType> dietaryPreferences = criteria.getDietaryPreferences();

        // Meal DB does not support filtering for gluten-free or dairy free dietary preferences,
        // so if these preferences are present, empty set is returned.
        if (MealDbUtils.hasUnsupportedPreferences(dietaryPreferences)) {
            return Set.of();
        }

        List<String> ingredients = criteria.getIngredients();

        if (ingredients != null && !ingredients.isEmpty()) {
            for (String ing : ingredients) {
                Set<MealDbSummary> mealsByIngredient = fetchMealSummaries(FILTER_BY_INGREDIENT + ing);
                if (mealsByIngredient.isEmpty()) {
                    return Set.of();
                }
                mealSets.add(mealsByIngredient);
            }
        }

        if (cuisine != null) {
            List<String> possibleAreas = MealDbMapper.mapToDetailedCuisines(cuisine);
            Set<MealDbSummary> cuisineMeals = new HashSet<>();
            for (String area : possibleAreas) {
                cuisineMeals.addAll(fetchMealSummaries(FILTER_BY_CUISINE + area));
            }
            mealSets.add(cuisineMeals);
        }

        if (dietaryPreferences != null && !dietaryPreferences.isEmpty()) {
            if (dietaryPreferences.contains(DietaryPreferenceType.VEGAN) && dietaryPreferences.contains(DietaryPreferenceType.VEGETARIAN)) {
                mealSets.add(fetchMealSummaries(FILTER_BY_CATEGORY + "Vegan"));
            } else {
                for (DietaryPreferenceType preference : dietaryPreferences) {
                    if (preference == DietaryPreferenceType.VEGAN || preference == DietaryPreferenceType.VEGETARIAN) {
                        mealSets.add(fetchMealSummaries(FILTER_BY_CATEGORY + preference.getDisplayName()));
                    }
                }
            }
        }

        if (mealSets.isEmpty()) return Set.of();

        Set<MealDbSummary> result = new HashSet<>(mealSets.get(0));
        for (int i = 1; i < mealSets.size(); i++) {
            result.retainAll(mealSets.get(i));
        }

        return result;
    }

    private Set<MealDbSummary> fetchMealSummaries(String url) {
        try {
            MealDbSummaryResponse response = restTemplate.getForObject(url, MealDbSummaryResponse.class);
            if (response.getMeals() != null) {
                return new HashSet<>(response.getMeals());
            }
            return Set.of();
        } catch (Exception e) {
            throw new MealDbServiceUnavailableException("MealDB API is currently unavailable", e);
        }
    }

    public ExternalRecipeDetailsDto getRecipeDetailById(String id) {
        String url = LOOKUP_BY_ID + id;
        MealDbDetailsResponse response = restTemplate.getForObject(url, MealDbDetailsResponse.class);

        if (response.getMeals() == null || response.getMeals().isEmpty()) {
            throw new MealNotFoundException("Meal with id " + id + " not found");
        }

        try {
            MealDbDetails mealDbDetails = response.getMeals().get(0);
            return mealDbMapper.mapMealDbDetailsToRecipeDetailsDto(mealDbDetails);
        } catch (Exception e) {
            throw new MealDbServiceUnavailableException("MealDB API is currently unavailable", e);
        }
    }
}
