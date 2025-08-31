package io.github.joannazadlo.recipedash.model.userPreferences;

import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferencesDto {
    private List<String> preferredIngredients;
    private CuisineType cuisine;
    private List<DietaryPreferenceType> dietaryPreferences;
    private Boolean excludeDisliked;

    public static UserPreferencesDto empty() {
        return UserPreferencesDto.builder()
                .preferredIngredients(Collections.emptyList())
                .cuisine(null)
                .dietaryPreferences(Collections.emptyList())
                .excludeDisliked(false)
                .build();
    }
}
