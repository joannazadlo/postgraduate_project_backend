package io.github.joannazadlo.recipedash.model.searchCriteria;

import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ParameterObject
public class AllSourcesSearchCriteriaDto {

    @Size(max = 10, message = "Maximum 10 ingredients allowed")
    private List<@Size(max = 50, message = "Ingredient must be at most 50 characters") String> ingredients;

    private CuisineType cuisine;

    private List<DietaryPreferenceType> dietaryPreferences;

    private String source;

    private Boolean excludeDisliked;
}
