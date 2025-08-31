package io.github.joannazadlo.recipedash.controller;

import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalIngredientDto;
import io.github.joannazadlo.recipedash.model.externalRecipe.ExternalRecipeDetailsDto;
import io.github.joannazadlo.recipedash.service.TastyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TastyController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class TastyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TastyService tastyService;

    @Test
    void getRecipeById_ShouldReturnRecipe() throws Exception {
        ExternalRecipeDetailsDto recipeDto = ExternalRecipeDetailsDto.builder()
                .id("1")
                .title("Chicken with rice")
                .imageSource("https://images.example.com/recipes/chicken-rice.jpg")
                .ingredients(List.of(ExternalIngredientDto.builder()
                                .name("chicken")
                                .build(),
                        ExternalIngredientDto.builder()
                                .name("rice")
                                .quantity("150g")
                                .build()))
                .steps(List.of(
                        "Heat oil in a large pan over medium heat.",
                        "Add chicken and cook until browned.",
                        "Stir in rice and water, cover and simmer for 20 minutes."
                ))
                .cuisine(CuisineType.AFRICAN)
                .dietaryPreferences(List.of(DietaryPreferenceType.GLUTEN_FREE))
                .build();

        when(tastyService.getRecipeDetailById("1")).thenReturn(recipeDto);

        mockMvc.perform(get("/tasty/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Chicken with rice"))
                .andExpect(jsonPath("$.imageSource").value("https://images.example.com/recipes/chicken-rice.jpg"))
                .andExpect(jsonPath("$.ingredients[0].name").value("chicken"))
                .andExpect(jsonPath("$.ingredients[1].name").value("rice"))
                .andExpect(jsonPath("$.ingredients[1].quantity").value("150g"))
                .andExpect(jsonPath("$.steps[0]").value("Heat oil in a large pan over medium heat."))
                .andExpect(jsonPath("$.steps[1]").value("Add chicken and cook until browned."))
                .andExpect(jsonPath("$.steps[2]").value("Stir in rice and water, cover and simmer for 20 minutes."))
                .andExpect(jsonPath("$.cuisine").value("African"))
                .andExpect(jsonPath("$.dietaryPreferences[0]").value("Gluten-Free"));
    }

    @Test
    void getRecipeById_ShouldReturnBadRequest_WhenIdIsBlank() throws Exception {
        mockMvc.perform(get("/tasty/ ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("getRecipeById.id: must not be blank"));
    }
}
