package io.github.joannazadlo.recipedash.model.recipe;

import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.recipeIngredient.RecipeIngredientUpdateDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeUpdateDto {

    private Long id;

    @NotBlank(message = "Recipe title is required")
    @Size(max = 100, message = "Title can't exceed 100 characters")
    private String title;

    @Valid
    @Size(max = 50, message = "Too many ingredients (max 50)")
    private List<RecipeIngredientUpdateDto> ingredients;

    @Size(max = 50, message = "Too many steps (max 50)")
    private List<@Size(max = 5000, message = "Each step must be at most 5000 characters") String> steps;

    @Size(max = 10, message = "You can specify at most 10 dietary preferences")
    private List<DietaryPreferenceType> dietaryPreferences;

    @Size(max = 30, message = "Cooking time must be at most 30 characters")
    private String cookingTime;

    private CuisineType cuisine;

    @JsonProperty("isPublic")
    private boolean publicRecipe;
}

