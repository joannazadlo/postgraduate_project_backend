package io.github.joannazadlo.recipedash.model.recipe;

import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.recipeIngredient.RecipeIngredientDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RecipeDetailsDto extends RecipeSummaryDto {
    private List<RecipeIngredientDto> ingredients;
    private List<String> steps;
    private List<DietaryPreferenceType> dietaryPreferences;
    private String cookingTime;
    private CuisineType cuisine;
}
