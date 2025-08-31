package io.github.joannazadlo.recipedash.model.userIngredient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserIngredientDto {
    private Long id;
    private String ingredient;
}
