package io.github.joannazadlo.recipedash.controller;

import io.github.joannazadlo.recipedash.model.recipe.RecipeSummaryWithUserDto;
import io.github.joannazadlo.recipedash.model.searchCriteria.RecipeSearchCriteriaDto;
import io.github.joannazadlo.recipedash.service.AdminRecipeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Admin Recipes", description = "API for admin to view recipes of all users")
@RestController
@RequestMapping("/admin/recipes")
@RequiredArgsConstructor
@Validated
public class AdminRecipeController {

    private final AdminRecipeService adminRecipeService;

    @GetMapping
    public ResponseEntity<List<RecipeSummaryWithUserDto>> getAllRecipes(
            @Valid @ParameterObject @ModelAttribute RecipeSearchCriteriaDto criteria
    ) {
        List<RecipeSummaryWithUserDto> recipes;

        if (criteria.isEmpty()) {
            recipes = adminRecipeService.getAllRecipes();
        } else {
            recipes = adminRecipeService.searchRecipesForAdmin(criteria);
        }

        return ResponseEntity.ok(recipes);
    }
}
