package io.github.joannazadlo.recipedash.utils;

import io.github.joannazadlo.recipedash.mapper.TastyMapper;
import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.searchCriteria.AllSourcesSearchCriteriaDto;
import io.github.joannazadlo.recipedash.model.tasty.TastyRecipeRaw;
import io.github.joannazadlo.recipedash.model.tasty.TastyTag;

import java.util.List;
import java.util.Objects;

public class TastyUtils {

    private TastyUtils() {}

    public static String buildSearchQuery(AllSourcesSearchCriteriaDto criteria) {
        String query;
        if (criteria.getIngredients() != null && !criteria.getIngredients().isEmpty()) {
            query = criteria.getIngredients().get(0);
        } else if (criteria.getCuisine() != null) {
            query = criteria.getCuisine().name().toLowerCase();
        } else if (criteria.getDietaryPreferences() != null && !criteria.getDietaryPreferences().isEmpty()) {
            query = criteria.getDietaryPreferences().get(0).name().toLowerCase();
        } else {
            query = "";
        }
        return query;
    }

    public static List<String> extractRequiredIngredients(AllSourcesSearchCriteriaDto criteria) {
        return (criteria.getIngredients() == null || criteria.getIngredients().isEmpty()) ?
                List.of() :
                criteria.getIngredients().stream()
                        .map(String::toLowerCase)
                        .toList();
    }

    public static boolean filterByCuisine(AllSourcesSearchCriteriaDto criteria, TastyRecipeRaw recipe) {
        if (criteria.getCuisine() != null) {
            String cuisineRaw = recipe.getTags() == null ? null :
                    recipe.getTags().stream()
                            .filter(tag -> "cuisine".equalsIgnoreCase(tag.getType()))
                            .map(TastyTag::getName)
                            .findFirst()
                            .orElse(null);

            CuisineType mappedCuisine = TastyMapper.mapTastyCuisineToBroadType(cuisineRaw);
            return criteria.getCuisine().equals(mappedCuisine);
        }
        return true;
    }

    public static boolean filterByIngredients(TastyRecipeRaw recipe, List<String> requiredIngredients) {
        if (requiredIngredients.isEmpty()) {
            return true;
        }

        List<String> recipeIngredients = recipe.getSections().stream()
                .flatMap(section -> section.getComponents().stream())
                .map(component -> component.getIngredient().getName().toLowerCase())
                .toList();

        return requiredIngredients.stream().allMatch(required ->
                recipeIngredients.stream().anyMatch(recipeIng ->
                        recipeIng.contains(required.toLowerCase())
                )
        );
    }

    public static boolean filterByDietaryPreferences(AllSourcesSearchCriteriaDto criteria, TastyRecipeRaw recipe) {
        if (criteria.getDietaryPreferences() != null && !criteria.getDietaryPreferences().isEmpty()) {
            List<DietaryPreferenceType> recipeDietTags = recipe.getTags() == null ? List.of() :
                    recipe.getTags().stream()
                            .filter(tag -> "dietary".equalsIgnoreCase(tag.getType()))
                            .map(tag -> DietaryPreferenceType.fromString(tag.getName()))
                            .filter(Objects::nonNull)
                            .toList();

            return criteria.getDietaryPreferences().stream().anyMatch(recipeDietTags::contains);
        }
        return true;
    }
}
