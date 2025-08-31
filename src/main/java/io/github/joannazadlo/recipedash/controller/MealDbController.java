package io.github.joannazadlo.recipedash.controller;

import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeDetailsDto;
import io.github.joannazadlo.recipedash.service.MealDbService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MealDB Recipes", description = "API for integration with MealDB external recipe database")
@RestController
@RequestMapping("/mealdb")
@RequiredArgsConstructor
@Validated
public class MealDbController {

    private final MealDbService mealDbService;

    @GetMapping("/{id}")
    public ResponseEntity<ExternalRecipeDetailsDto> getRecipeById(
            @PathVariable @NotBlank String id) {
        return ResponseEntity.ok(mealDbService.getRecipeDetailById(id));
    }
}
