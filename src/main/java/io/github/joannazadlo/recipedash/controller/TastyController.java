package io.github.joannazadlo.recipedash.controller;

import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeDetailsDto;
import io.github.joannazadlo.recipedash.service.TastyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tasty Recipes", description = "API for integration with Tasty recipe service")
@RestController
@RequestMapping("/tasty")
@RequiredArgsConstructor
@Validated
public class TastyController {

    private final TastyService tastyService;

    @GetMapping("/{id}")
    public ResponseEntity<ExternalRecipeDetailsDto> getRecipeById(
            @PathVariable @NotBlank String id) {
        return ResponseEntity.ok(tastyService.getRecipeDetailById(id));
    }
}
