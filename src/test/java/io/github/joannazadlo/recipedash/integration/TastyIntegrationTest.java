package io.github.joannazadlo.recipedash.integration;

import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeSummaryDto;
import io.github.joannazadlo.recipedash.model.searchCriteria.AllSourcesSearchCriteriaDto;
import io.github.joannazadlo.recipedash.service.TastyService;
import io.github.joannazadlo.recipedash.model.tasty.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TastyIntegrationTest {

    @Autowired
    private TastyService tastyService;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void searchMeals_shouldReturnMappedResults() throws Exception {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .ingredients(List.of("tomato"))
                .cuisine(CuisineType.AFRICAN)
                .dietaryPreferences(List.of(DietaryPreferenceType.VEGETARIAN))
                .build();

        TastyRecipeRaw recipe1 = TastyRecipeRaw.builder()
                .canonical_id("recipe:1")
                .name("African Vegetarian Meal with Tomato")
                .thumbnail_url("http://image1.jpg")
                .instructions(List.of(
                        Instruction.builder().display_text("Cook well").build()
                ))
                .sections(List.of(
                        Section.builder()
                                .components(List.of(
                                        Component.builder()
                                                .ingredient(Ingredient.builder().name("tomato").build())
                                                .build()
                                ))
                                .build()
                ))
                .tags(List.of(
                        new TastyTag("African", "cuisine"),
                        new TastyTag("Vegetarian", "dietary")
                ))
                .build();

        TastyRecipeRaw recipe2 = TastyRecipeRaw.builder()
                .canonical_id("recipe:2")
                .name("African Non-Vegetarian Meal with Tomato")
                .thumbnail_url("http://image2.jpg")
                .instructions(List.of(
                        Instruction.builder().display_text("Cook well").build()
                ))
                .sections(List.of(
                        Section.builder()
                                .components(List.of(
                                        Component.builder()
                                                .ingredient(Ingredient.builder().name("tomato").build())
                                                .build()
                                ))
                                .build()
                ))
                .tags(List.of(
                        new TastyTag("African", "cuisine"),
                        new TastyTag("Non-Vegetarian", "dietary")
                ))
                .build();

        TastyRecipeRaw recipe3 = TastyRecipeRaw.builder()
                .canonical_id("recipe:3")
                .name("African Vegetarian Vegan Meal")
                .thumbnail_url("http://image3.jpg")
                .instructions(List.of(
                        Instruction.builder().display_text("Cook well").build()
                ))
                .sections(List.of(
                        Section.builder()
                                .components(List.of(
                                        Component.builder()
                                                .ingredient(Ingredient.builder().name("tomato").build())
                                                .build()
                                ))
                                .build()
                ))
                .tags(List.of(
                        new TastyTag("African", "cuisine"),
                        new TastyTag("Vegetarian", "dietary"),
                        new TastyTag("vegan", "dietary")
                ))
                .build();

        TastyRecipeResponse tastyResponse = new TastyRecipeResponse();
        tastyResponse.setResults(List.of(recipe1, recipe2, recipe3));

        ResponseEntity<TastyRecipeResponse> responseEntity = new ResponseEntity<>(tastyResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(TastyRecipeResponse.class)
        )).thenReturn(responseEntity);

        List<ExternalRecipeSummaryDto> results = tastyService.searchMeals(criteria);

        assertEquals(2, results.size());
        assertEquals("African Vegetarian Meal with Tomato", results.get(0).getTitle());
        assertEquals("African Vegetarian Vegan Meal", results.get(1).getTitle());
        assertEquals("1", results.get(0).getId());
        assertEquals("3", results.get(1).getId());
        assertEquals("http://image1.jpg", results.get(0).getImageSource());
        assertEquals("http://image3.jpg", results.get(1).getImageSource());

        assertTrue(results.stream().noneMatch(r -> r.getId().equals("2")));
    }

    @Test
    void searchMeals_shouldReturnEmptyList_WhenApiReturnsNoResults() {
        AllSourcesSearchCriteriaDto criteria = AllSourcesSearchCriteriaDto.builder()
                .ingredients(List.of("coconut"))
                .build();

        TastyRecipeResponse tastyRecipeResponse = new TastyRecipeResponse();
        tastyRecipeResponse.setResults(List.of());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(TastyRecipeResponse.class)
        )).thenReturn(new ResponseEntity<>(tastyRecipeResponse, HttpStatus.OK));

        List<ExternalRecipeSummaryDto> results = tastyService.searchMeals(criteria);

        assertTrue(results.isEmpty());
    }
}
