package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.model.searchCriteria.AllSourcesSearchCriteriaDto;
import io.github.joannazadlo.recipedash.model.searchCriteria.RecipeSearchCriteriaDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeSummaryDto;
import io.github.joannazadlo.recipedash.model.recipe.SearchRecipeDto;
import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeSummaryDto;
import io.github.joannazadlo.recipedash.repository.OpinionRepository;
import io.github.joannazadlo.recipedash.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MealDbService mealDBService;
    private final RecipeService recipeService;
    private final TastyService tastyService;
    private final OpinionRepository opinionRepository;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<SearchRecipeDto> searchRecipes(
            AllSourcesSearchCriteriaDto criteria) {

        List<SearchRecipeDto> allRecipes = new ArrayList<>();
        if (criteria.getSource() == null || criteria.getSource().equalsIgnoreCase("MealDb")) {
            allRecipes.addAll(mealDbRecipes(criteria));
        }
        if (criteria.getSource() == null || criteria.getSource().equalsIgnoreCase("Tasty")) {
            allRecipes.addAll(tastyRecipes(criteria));
        }
        if (criteria.getSource() == null || criteria.getSource().equalsIgnoreCase("User")) {
            allRecipes.addAll(usersPublicRecipes(criteria));
        }

        if (criteria.getExcludeDisliked() != null && criteria.getExcludeDisliked()) {
            String uid = SecurityUtils.getCurrentUser().getUid();
            allRecipes = filterOutDislikedRecipes(allRecipes, uid);
        }

        return allRecipes;
    }

    private List<SearchRecipeDto> mealDbRecipes(
            AllSourcesSearchCriteriaDto criteria
    ) {
        List<SearchRecipeDto> mealDbRecipes = new ArrayList<>();
        for (ExternalRecipeSummaryDto recipe : mealDBService.searchMeals(criteria)) {
            mealDbRecipes.add(SearchRecipeDto.builder()
                    .id(recipe.getId())
                    .title(recipe.getTitle())
                    .imageSource(recipe.getImageSource())
                    .source("MealDb").build());
        }
        return mealDbRecipes;
    }

    private List<SearchRecipeDto> tastyRecipes(
            AllSourcesSearchCriteriaDto criteria
    ) {
        List<SearchRecipeDto> tastyRecipes = new ArrayList<>();
        for (ExternalRecipeSummaryDto recipe : tastyService.searchMeals(criteria)) {
            tastyRecipes.add(SearchRecipeDto.builder()
                    .id(recipe.getId())
                    .title(recipe.getTitle())
                    .imageSource(recipe.getImageSource())
                    .source("Tasty").build());
        }
        return tastyRecipes;
    }

    private List<SearchRecipeDto> usersPublicRecipes(
            AllSourcesSearchCriteriaDto criteria
    ) {
        RecipeSearchCriteriaDto recipeCriteria = RecipeSearchCriteriaDto.builder()
                .ingredients(criteria.getIngredients())
                .cuisine(criteria.getCuisine())
                .dietaryPreferences(criteria.getDietaryPreferences())
                .isPublic(true)
                .build();

        List<SearchRecipeDto> usersPublicRecipes = new ArrayList<>();
        for (RecipeSummaryDto recipe : recipeService.searchPublicRecipes(recipeCriteria)) {
            usersPublicRecipes.add(SearchRecipeDto.builder()
                    .id(String.valueOf(recipe.getId()))
                    .title(recipe.getTitle())
                    .imageSource(recipe.getImageSource())
                    .source("User").build());
        }
        return usersPublicRecipes;
    }

    private List<SearchRecipeDto> filterOutDislikedRecipes(List<SearchRecipeDto> recipes, String userId) {
        List<String> dislikedKeys = opinionRepository.findDislikedRecipeKeysByUser(userId);
        Set<String> dislikedSet = new HashSet<>(dislikedKeys);

        return recipes.stream()
                .filter(recipe -> !dislikedSet.contains(recipe.getId() + "|" + recipe.getSource()))
                .collect(Collectors.toList());
    }
}
