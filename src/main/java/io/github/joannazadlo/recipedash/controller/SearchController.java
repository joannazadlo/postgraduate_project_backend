package io.github.joannazadlo.recipedash.controller;

import io.github.joannazadlo.recipedash.model.searchCriteria.AllSourcesSearchCriteriaDto;
import io.github.joannazadlo.recipedash.model.recipe.SearchRecipeDto;
import io.github.joannazadlo.recipedash.service.SearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Search", description = "API related to searching recipes across different sources")
@RestController
@RequestMapping("recipes/search")
@RequiredArgsConstructor
@Validated
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<SearchRecipeDto>> searchRecipes(
            @Valid @ParameterObject @ModelAttribute AllSourcesSearchCriteriaDto criteria
    ) {
        List<SearchRecipeDto> recipes = searchService.searchRecipes(criteria);
        return ResponseEntity.ok(recipes);
    }
}
