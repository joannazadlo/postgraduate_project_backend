package io.github.joannazadlo.recipedash.controller;

import io.github.joannazadlo.recipedash.model.searchCriteria.RecipeSearchCriteriaDto;
import io.github.joannazadlo.recipedash.service.RecipeService;
import io.github.joannazadlo.recipedash.model.recipe.multipart.CreateRecipeWithImageDto;
import io.github.joannazadlo.recipedash.model.recipe.multipart.UpdateRecipeWithImageDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeCreateDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeDetailsDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeSummaryDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;


import java.util.List;

@Tag(name = "Recipes", description = "API related to managing recipes (creation, update, deletion, retrieval)")
@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
@Validated
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<List<RecipeSummaryDto>> getRecipes(
            @Valid @ParameterObject @ModelAttribute RecipeSearchCriteriaDto criteria
    ) {
        List<RecipeSummaryDto> recipes;

        if (criteria.isEmpty()) {
            recipes = recipeService.getRecipesForCurrentUser();
        } else {
            recipes = recipeService.searchRecipesForUser(criteria);
        }

        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDetailsDto> getRecipeById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    @PostMapping
    public ResponseEntity<RecipeDetailsDto> createRecipe(@RequestBody @Valid RecipeCreateDto recipe) {
        RecipeDetailsDto createdRecipe = recipeService.createRecipe(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = CreateRecipeWithImageDto.class),
                            encoding = {
                                    @Encoding(name = "recipe", contentType = "application/json"),
                                    @Encoding(name = "image", contentType = "image/*")
                            }
                    )
            )
    )
    public ResponseEntity<RecipeDetailsDto> createRecipeWithImage(
            @RequestPart("recipe") @Valid RecipeCreateDto recipe,
            @RequestPart("image") MultipartFile imageFile
    ) {
        RecipeDetailsDto createdRecipe = recipeService.createRecipeWithImage(recipe, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDetailsDto> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody RecipeUpdateDto updatedRecipe
    ) {
        RecipeDetailsDto updated = recipeService.updateRecipe(id, updatedRecipe);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/image")
    @Operation(
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = UpdateRecipeWithImageDto.class),
                            encoding = {
                                    @Encoding(name = "recipe", contentType = "application/json"),
                                    @Encoding(name = "image", contentType = "image/*")
                            }
                    )
            )
    )
    public ResponseEntity<RecipeDetailsDto> updateRecipeWithImage(
            @PathVariable Long id,
            @RequestPart("recipe") @Valid RecipeUpdateDto updatedRecipe,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageRemoved", required = false, defaultValue = "false") boolean imageRemoved
    ) {
        RecipeDetailsDto result = recipeService.updateRecipeWithImage(id, updatedRecipe, imageFile, imageRemoved);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
}
