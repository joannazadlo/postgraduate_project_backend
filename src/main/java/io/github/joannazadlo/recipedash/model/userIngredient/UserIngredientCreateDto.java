package io.github.joannazadlo.recipedash.model.userIngredient;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserIngredientCreateDto {

    @NotBlank(message = "Ingredient cannot be blank")
    private String ingredient;
}
