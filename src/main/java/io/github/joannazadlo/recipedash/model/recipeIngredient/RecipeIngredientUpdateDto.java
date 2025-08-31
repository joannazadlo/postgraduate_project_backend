package io.github.joannazadlo.recipedash.model.recipeIngredient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredientUpdateDto {

    private Long id;

    @NotBlank(message = "Name cannot be empty")
    @Size(max = 50, message = "Ingredient name must be at most 50 characters")
    private String name;

    @Size(max = 30, message = "Quantity must be at most 30 characters")
    private String quantity;
}
