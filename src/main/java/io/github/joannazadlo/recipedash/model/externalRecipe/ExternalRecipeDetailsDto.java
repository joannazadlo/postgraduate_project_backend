package io.github.joannazadlo.recipedash.model.externalRecipe;

import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
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
public class ExternalRecipeDetailsDto extends ExternalRecipeSummaryDto {
    private List<ExternalIngredientDto> ingredients;
    private List<String> steps;
    private CuisineType cuisine;
    private List<DietaryPreferenceType> dietaryPreferences;
}
