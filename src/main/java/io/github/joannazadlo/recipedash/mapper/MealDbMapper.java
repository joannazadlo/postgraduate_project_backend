package io.github.joannazadlo.recipedash.mapper;

import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalIngredientDto;
import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeDetailsDto;
import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeSummaryDto;
import io.github.joannazadlo.recipedash.model.mealdb.MealDbDetails;
import io.github.joannazadlo.recipedash.model.mealdb.MealDbSummary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MealDbMapper {

    public ExternalRecipeSummaryDto mapMealDbSummaryToRecipeSummaryDto(MealDbSummary summary) {
        ExternalRecipeSummaryDto recipe = new ExternalRecipeSummaryDto();
        recipe.setId(summary.getIdMeal());
        recipe.setTitle(summary.getStrMeal());
        recipe.setImageSource(summary.getStrMealThumb());
        return recipe;
    }

    public ExternalRecipeDetailsDto mapMealDbDetailsToRecipeDetailsDto(MealDbDetails details) {
        ExternalRecipeDetailsDto recipe = new ExternalRecipeDetailsDto();

        recipe.setId(details.getIdMeal());
        recipe.setTitle(details.getStrMeal());
        recipe.setImageSource(details.getStrMealThumb());
        recipe.setCuisine(mapFromDetailedCuisines(details.getStrArea()));

        List<DietaryPreferenceType> dietaryPreferences = mapDietaryPreferences(details.getStrCategory());
        recipe.setDietaryPreferences(dietaryPreferences);

        List<String> ingredientValues = Arrays.asList(
                details.getStrIngredient1(), details.getStrIngredient2(), details.getStrIngredient3(),
                details.getStrIngredient4(), details.getStrIngredient5(), details.getStrIngredient6(),
                details.getStrIngredient7(), details.getStrIngredient8(), details.getStrIngredient9(),
                details.getStrIngredient10(), details.getStrIngredient11(), details.getStrIngredient12(),
                details.getStrIngredient13(), details.getStrIngredient14(), details.getStrIngredient15(),
                details.getStrIngredient16(), details.getStrIngredient17(), details.getStrIngredient18(),
                details.getStrIngredient19(), details.getStrIngredient20()
        );

        List<String> measureValues = Arrays.asList(
                details.getStrMeasure1(), details.getStrMeasure2(), details.getStrMeasure3(),
                details.getStrMeasure4(), details.getStrMeasure5(), details.getStrMeasure6(),
                details.getStrMeasure7(), details.getStrMeasure8(), details.getStrMeasure9(),
                details.getStrMeasure10(), details.getStrMeasure11(), details.getStrMeasure12(),
                details.getStrMeasure13(), details.getStrMeasure14(), details.getStrMeasure15(),
                details.getStrMeasure16(), details.getStrMeasure17(), details.getStrMeasure18(),
                details.getStrMeasure19(), details.getStrMeasure20()
        );

        List<ExternalIngredientDto> mealDbIngredients = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            String name = ingredientValues.get(i);
            String quantity = measureValues.get(i);
            if (name != null && !name.isBlank()) {
                mealDbIngredients.add(ExternalIngredientDto.builder()
                        .name(name.trim())
                        .quantity(quantity != null ? quantity.trim() : "")
                        .build());
            }
        }

        recipe.setIngredients(mealDbIngredients);

        List<String> steps = new ArrayList<>();
        if (details.getStrInstructions() != null) {
            steps = Arrays.stream(details.getStrInstructions().split("\\r?\\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
        recipe.setSteps(steps);

        return recipe;
    }

    public List<DietaryPreferenceType> mapDietaryPreferences(String category) {
        List<DietaryPreferenceType> preferences = new ArrayList<>();

        if (category == null || category.isBlank()) {
            return preferences;
        }

        if (category.equalsIgnoreCase("vegan")) {
            preferences.add(DietaryPreferenceType.VEGAN);
            preferences.add(DietaryPreferenceType.VEGETARIAN);
        } else if (category.equalsIgnoreCase("vegetarian")) {
            preferences.add(DietaryPreferenceType.VEGETARIAN);
        }

        return preferences;
    }

    public static CuisineType mapFromDetailedCuisines(String detailedCuisine) {
        if (detailedCuisine == null) return CuisineType.OTHER;

        String normalized = detailedCuisine.trim().toLowerCase();

        return switch (normalized) {
            case "american", "canadian" -> CuisineType.NORTH_AMERICAN;
            case "chinese", "filipino", "indian", "japanese", "malaysian", "thai", "vietnamese" -> CuisineType.ASIAN;
            case "french", "greek", "croatian", "dutch", "italian", "polish", "portuguese", "russian", "spanish",
                    "ukrainian", "british", "irish" -> CuisineType.EUROPEAN;
            case "jamaican", "caribbean" -> CuisineType.CARIBBEAN;
            case "egyptian", "kenyan", "moroccan", "tunisian" -> CuisineType.AFRICAN;
            case "turkish", "middle eastern" -> CuisineType.MIDDLE_EASTERN;
            case "mexican", "uruguayan", "central american", "south american" -> CuisineType.CENTRAL_SOUTH_AMERICAN;
            case "jewish" -> CuisineType.JEWISH;
            default -> CuisineType.OTHER;
        };
    }

    public static List<String> mapToDetailedCuisines(CuisineType broadType) {
        return switch (broadType) {
            case NORTH_AMERICAN -> List.of("American", "Canadian");
            case ASIAN -> List.of("Chinese", "Filipino", "Indian", "Japanese", "Malaysian", "Thai", "Vietnamese");
            case EUROPEAN ->
                    List.of("French", "Greek", "Croatian", "Dutch", "Italian", "Polish", "Portuguese", "Russian", "Spanish", "Ukrainian", "British", "Irish");
            case CARIBBEAN -> List.of("Jamaican", "Caribbean");
            case AFRICAN -> List.of("Egyptian", "Kenyan", "Moroccan", "Tunisian");
            case MIDDLE_EASTERN -> List.of("Turkish", "Middle Eastern");
            case CENTRAL_SOUTH_AMERICAN -> List.of("Mexican", "Uruguayan", "Central American", "South American");
            case JEWISH -> List.of("Jewish");
            default -> List.of();
        };
    }
}
