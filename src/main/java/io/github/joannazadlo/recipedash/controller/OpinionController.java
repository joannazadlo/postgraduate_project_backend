package io.github.joannazadlo.recipedash.controller;

import io.github.joannazadlo.recipedash.model.rating.SaveOpinionDto;
import io.github.joannazadlo.recipedash.model.rating.RecipeRatingDto;
import io.github.joannazadlo.recipedash.service.OpinionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Opinions", description = "API for managing user opinions on recipes")
@RestController
@RequestMapping("/opinions")
@RequiredArgsConstructor
@Validated
public class OpinionController {

    private final OpinionService opinionService;

    @PutMapping("/{recipeSource}/{recipeId}")
    public ResponseEntity<RecipeRatingDto> saveOpinion(
            @PathVariable String recipeSource,
            @PathVariable String recipeId,
            @Valid @RequestBody SaveOpinionDto opinion
    ) {
        RecipeRatingDto recipeRating = opinionService.saveOpinion(recipeSource, recipeId, opinion);
        return ResponseEntity.ok(recipeRating);
    }

    @DeleteMapping("/{recipeSource}/{recipeId}")
    public ResponseEntity<RecipeRatingDto> deleteOpinion(
            @PathVariable String recipeSource,
            @PathVariable String recipeId
    ) {
        RecipeRatingDto recipeRating = opinionService.deleteOpinion(recipeId, recipeSource);
        return ResponseEntity.ok(recipeRating);
    }

    @GetMapping("/{recipeSource}/{recipeId}")
    public ResponseEntity<RecipeRatingDto> getRecipeRating(
            @PathVariable String recipeSource,
            @PathVariable String recipeId
    ) {
        RecipeRatingDto recipeRating = opinionService.getRecipeRating(recipeId, recipeSource);
        return ResponseEntity.ok(recipeRating);
    }
}
