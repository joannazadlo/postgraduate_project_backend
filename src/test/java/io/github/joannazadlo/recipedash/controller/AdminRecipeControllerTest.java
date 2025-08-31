package io.github.joannazadlo.recipedash.controller;

import io.github.joannazadlo.recipedash.model.recipe.RecipeSummaryWithUserDto;
import io.github.joannazadlo.recipedash.service.AdminRecipeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminRecipeController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class AdminRecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminRecipeService adminRecipeService;

    @Test
    void getRecipes_withoutCriteria_shouldReturnAllRecipes() throws Exception {
        List<RecipeSummaryWithUserDto> mockRecipes = List.of(
                RecipeSummaryWithUserDto.builder()
                        .id(1L)
                        .title("Chicken with rice")
                        .imageSource("https://images.example.com/recipes/chicken-rice.jpg")
                        .publicRecipe(true)
                        .createdAt(LocalDateTime.of(2025, 7, 18, 12, 0))
                        .userEmail("tomek.kowalski@gmail.com")
                        .build(),
                RecipeSummaryWithUserDto.builder()
                        .id(2L)
                        .title("Pasta")
                        .imageSource("https://images.example.com/recipes/pasta.jpg")
                        .publicRecipe(false)
                        .createdAt(LocalDateTime.of(2025, 7, 17, 8, 30))
                        .userEmail("basia@onet.pl")
                        .build()
        );

        when(adminRecipeService.getAllRecipes()).thenReturn(mockRecipes);

        mockMvc.perform(get("/admin/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Chicken with rice"))
                .andExpect(jsonPath("$[0].imageSource").value("https://images.example.com/recipes/chicken-rice.jpg"))
                .andExpect(jsonPath("$[0].isPublic").value(true))
                .andExpect(jsonPath("$[0].createdAt").value("2025-07-18T12:00:00"))
                .andExpect(jsonPath("$[0].userEmail").value("tomek.kowalski@gmail.com"))

                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Pasta"))
                .andExpect(jsonPath("$[1].imageSource").value("https://images.example.com/recipes/pasta.jpg"))
                .andExpect(jsonPath("$[1].isPublic").value(false))
                .andExpect(jsonPath("$[1].createdAt").value("2025-07-17T08:30:00"))
                .andExpect(jsonPath("$[1].userEmail").value("basia@onet.pl"));
    }

    @Test
    void getRecipes_withoutCriteriaAndNoRecipesExist_shouldReturnEmptyList() throws Exception {
        when(adminRecipeService.getAllRecipes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRecipes_withFilterCriteria_shouldReturnFilteredRecipes() throws Exception {
        RecipeSummaryWithUserDto dto = RecipeSummaryWithUserDto.builder()
                .id(1L)
                .title("Grilled Salmon")
                .imageSource("https://images.example.com/recipes/salmon.jpg")
                .userEmail("test@test.com")
                .build();

        when(adminRecipeService.searchRecipesForAdmin(any()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/admin/recipes")
                        .param("ingredients", "salmon", "lemon")
                        .param("cuisine", "ASIAN")
                        .param("dietaryPreferences", "GLUTEN_FREE", "DAIRY_FREE")
                        .param("isPublic", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Grilled Salmon"))
                .andExpect(jsonPath("$[0].imageSource").value("https://images.example.com/recipes/salmon.jpg"))
                .andExpect(jsonPath("$[0].userEmail").value("test@test.com"));
    }
}
