package io.github.joannazadlo.recipedash.mapper;

import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeDetailsDto;
import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeSummaryDto;
import io.github.joannazadlo.recipedash.model.tasty.TastyRecipeRaw;
import io.github.joannazadlo.recipedash.model.tasty.TastyTag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class TastyMapper {

    public ExternalRecipeSummaryDto mapTastySummaryToExternalRecipeSummaryDto(TastyRecipeRaw recipe) {
        ExternalRecipeSummaryDto detail = new ExternalRecipeSummaryDto();
        detail.setId(cleanCanonicalId(recipe.getCanonical_id()));
        detail.setTitle(recipe.getName());
        detail.setImageSource(recipe.getThumbnail_url());
        return detail;
    }

    public ExternalRecipeDetailsDto mapTastyDetailsToRecipeDetailsDto(TastyRecipeRaw recipe) {
        ExternalRecipeDetailsDto detail = new ExternalRecipeDetailsDto();
        detail.setId(cleanCanonicalId(recipe.getCanonical_id()));
        detail.setTitle(recipe.getName());
        detail.setImageSource(recipe.getThumbnail_url());
        detail.setIngredients(recipe.extractIngredients());
        detail.setSteps(recipe.extractInstructions());

        String cuisineRaw = recipe.getTags() == null ? null :
                recipe.getTags().stream()
                        .filter(tag -> "cuisine".equalsIgnoreCase(tag.getType()))
                        .map(TastyTag::getName)
                        .findFirst()
                        .orElse(null);
        detail.setCuisine(mapTastyCuisineToBroadType(cuisineRaw));

        List<DietaryPreferenceType> preferences = recipe.getTags() == null ? List.of() :
                recipe.getTags().stream()
                        .filter(tag -> "dietary".equalsIgnoreCase(tag.getType()))
                        .map(tag -> DietaryPreferenceType.fromString(tag.getName()))
                        .filter(Objects::nonNull)
                        .toList();
        detail.setDietaryPreferences(preferences);

        return detail;
    }

    private String cleanCanonicalId(String canonicalId) {
        if (canonicalId != null && canonicalId.startsWith("recipe:")) {
            return canonicalId.substring("recipe:".length());
        }
        return canonicalId;
    }

    public static CuisineType mapTastyCuisineToBroadType(String detailedCuisine) {
        if (detailedCuisine == null) return CuisineType.OTHER;

        try {
            return CuisineType.valueOf(detailedCuisine.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return CuisineType.OTHER;
        }
    }
}
