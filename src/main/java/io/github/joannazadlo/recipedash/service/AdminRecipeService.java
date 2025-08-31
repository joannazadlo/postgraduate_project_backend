package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.helper.RecipeHelper;
import io.github.joannazadlo.recipedash.model.recipe.RecipeSummaryWithUserDto;
import io.github.joannazadlo.recipedash.model.searchCriteria.RecipeSearchCriteriaDto;
import io.github.joannazadlo.recipedash.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class AdminRecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeHelper recipeUtils;

    @PreAuthorize("hasRole('ADMIN')")
    public List<RecipeSummaryWithUserDto> getAllRecipes() {
        return recipeRepository.findAllWithUser().stream()
                .map(recipe -> recipeUtils.mapRecipeWithUserDto(recipe, recipe.getUser()))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<RecipeSummaryWithUserDto> searchRecipesForAdmin(RecipeSearchCriteriaDto criteria) {
        return recipeRepository.findAllWithUser().stream()
                .filter(recipeUtils.recipeMatchesSearchCriteria(
                        criteria.getIngredients(),
                        criteria.getCuisine(),
                        criteria.getDietaryPreferences()))
                .filter(recipeUtils.filterByPublicStatus(criteria.getIsPublic()))
                .map(recipe -> recipeUtils.mapRecipeWithUserDto(recipe, recipe.getUser()))
                .collect(Collectors.toList());
    }
}
