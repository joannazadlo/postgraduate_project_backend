package io.github.joannazadlo.recipedash.model.mealdb;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "idMeal")
public class MealDbSummary {
    private String strMeal;
    private String strMealThumb;
    private String idMeal;
}
