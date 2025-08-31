package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.constants.TastyApiConstants;
import io.github.joannazadlo.recipedash.exception.tasty.TastyRecipeNotFoundException;
import io.github.joannazadlo.recipedash.exception.tasty.TastyServiceUnavailableException;
import io.github.joannazadlo.recipedash.mapper.TastyMapper;
import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeDetailsDto;
import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeSummaryDto;
import io.github.joannazadlo.recipedash.model.searchCriteria.AllSourcesSearchCriteriaDto;
import io.github.joannazadlo.recipedash.model.tasty.TastyRecipeRaw;
import io.github.joannazadlo.recipedash.model.tasty.TastyRecipeResponse;
import io.github.joannazadlo.recipedash.utils.TastyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TastyService {

    private final RestTemplate restTemplate;
    private final TastyMapper tastyMapper;

    @Value("${tasty.api.key}")
    private String apiKey;

    public List<ExternalRecipeSummaryDto> searchMeals(AllSourcesSearchCriteriaDto criteria) {
        HttpEntity<String> entity = buildHeadersEntity();

        String query = TastyUtils.buildSearchQuery(criteria);

        String url = TastyApiConstants.RECIPES_LIST + "?from=0&size=50" + "&q=" + query;

        try {
            ResponseEntity<TastyRecipeResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    TastyRecipeResponse.class
            );

            TastyRecipeResponse tastyResponse = response.getBody();

            if (tastyResponse.getResults() == null) {
                return List.of();
            }

            List<String> requiredIngredients = TastyUtils.extractRequiredIngredients(criteria);

            return tastyResponse.getResults().stream()
                    .filter(recipe -> recipe.getInstructions() != null && recipe.getSections() != null)
                    .filter(recipe -> TastyUtils.filterByCuisine(criteria, recipe))
                    .filter(recipe -> TastyUtils.filterByDietaryPreferences(criteria, recipe))
                    .filter(recipe -> TastyUtils.filterByIngredients(recipe, requiredIngredients))
                    .map(tastyMapper::mapTastySummaryToExternalRecipeSummaryDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new TastyServiceUnavailableException("Tasty API is currently unavailable", e);
        }
    }

    public ExternalRecipeDetailsDto getRecipeDetailById(String id) {
        HttpEntity<String> entity = buildHeadersEntity();

        String url = TastyApiConstants.RECIPE_INFO + id;

        try {
            ResponseEntity<TastyRecipeRaw> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    TastyRecipeRaw.class
            );

            TastyRecipeRaw recipe = response.getBody();

            if (recipe == null || recipe.getName() == null || recipe.getName().isEmpty()) {
                throw new TastyRecipeNotFoundException("Recipe with id " + id + " not found");
            }

            return tastyMapper.mapTastyDetailsToRecipeDetailsDto(recipe);
        } catch (TastyRecipeNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TastyServiceUnavailableException("Tasty API is currently unavailable", e);
        }
    }

    private HttpEntity<String> buildHeadersEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(TastyApiConstants.RAPIDAPI_KEY_HEADER, apiKey);
        headers.set(TastyApiConstants.RAPIDAPI_HOST_HEADER, TastyApiConstants.RAPIDAPI_HOST_VALUE);
        return new HttpEntity<>(headers);
    }
}
