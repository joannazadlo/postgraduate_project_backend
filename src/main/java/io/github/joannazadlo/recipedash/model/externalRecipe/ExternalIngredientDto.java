package io.github.joannazadlo.recipedash.model.externalRecipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExternalIngredientDto {
    private String name;
    private String quantity;
}
