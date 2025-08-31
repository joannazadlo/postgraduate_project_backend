package io.github.joannazadlo.recipedash.model.mealdb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealDbSummaryResponse {
    private List<MealDbSummary> meals;
}
