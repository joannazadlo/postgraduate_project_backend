package io.github.joannazadlo.recipedash.controller;

import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.userPreferences.UserPreferencesDto;
import io.github.joannazadlo.recipedash.service.UserPreferencesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserPreferencesController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class UserPreferencesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserPreferencesService userPreferencesService;

    @Test
    void saveUserPreferences_withValidData_returnsSavedPreferences() throws Exception {
        UserPreferencesDto savePreferencesDto = UserPreferencesDto.builder()
                .preferredIngredients(List.of("chicken", "tomato"))
                .cuisine(CuisineType.ASIAN)
                .dietaryPreferences(List.of(DietaryPreferenceType.GLUTEN_FREE))
                .excludeDisliked(true)
                .build();

        when(userPreferencesService.saveUserPreferences(Mockito.any())).thenReturn(savePreferencesDto);

        mockMvc.perform(put("/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savePreferencesDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cuisine").value("Asian"))
                .andExpect(jsonPath("$.dietaryPreferences[0]").value("Gluten-Free"))
                .andExpect(jsonPath("$.preferredIngredients[0]").value("chicken"))
                .andExpect(jsonPath("$.preferredIngredients[1]").value("tomato"))
                .andExpect(jsonPath("$.excludeDisliked").value(true));
    }

    @Test
    void saveUserPreferences_withEmptyListsAndNoCuisine_returnsSavedPreferences() throws Exception {
        UserPreferencesDto savePreferencesDto = UserPreferencesDto.builder()
                .preferredIngredients(List.of())
                .dietaryPreferences(List.of())
                .excludeDisliked(false)
                .build();

        when(userPreferencesService.saveUserPreferences(Mockito.any())).thenReturn(savePreferencesDto);

        mockMvc.perform(put("/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savePreferencesDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cuisine").doesNotExist())
                .andExpect(jsonPath("$.dietaryPreferences").isEmpty())
                .andExpect(jsonPath("$.preferredIngredients").isEmpty())
                .andExpect(jsonPath("$.excludeDisliked").value(false));
    }

    @Test
    void getUserPreferences_shouldReturnUserPreferences() throws Exception {
        UserPreferencesDto mockUserPreferences = UserPreferencesDto.builder()
                .preferredIngredients(List.of("chicken", "tomato"))
                .cuisine(CuisineType.ASIAN)
                .dietaryPreferences(List.of(DietaryPreferenceType.GLUTEN_FREE))
                .excludeDisliked(true)
                .build();

        when(userPreferencesService.getUserPreferences()).thenReturn(mockUserPreferences);

        mockMvc.perform(get("/preferences"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cuisine").value("Asian"))
                .andExpect(jsonPath("$.dietaryPreferences[0]").value("Gluten-Free"))
                .andExpect(jsonPath("$.preferredIngredients[0]").value("chicken"))
                .andExpect(jsonPath("$.preferredIngredients[1]").value("tomato"))
                .andExpect(jsonPath("$.excludeDisliked").value(true));
    }

    @Test
    void getUserPreferences_shouldReturnEmpty_whenNoPreferencesSet() throws Exception {
        when(userPreferencesService.getUserPreferences()).thenReturn(UserPreferencesDto.empty());

        mockMvc.perform(get("/preferences"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cuisine").doesNotExist())
                .andExpect(jsonPath("$.dietaryPreferences").isEmpty())
                .andExpect(jsonPath("$.preferredIngredients").isEmpty())
                .andExpect(jsonPath("$.excludeDisliked").value(false));
    }
}
