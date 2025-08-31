package io.github.joannazadlo.recipedash.controller;

import io.github.joannazadlo.recipedash.model.enums.UserOpinion;
import io.github.joannazadlo.recipedash.model.rating.RecipeRatingDto;
import io.github.joannazadlo.recipedash.model.rating.SaveOpinionDto;
import io.github.joannazadlo.recipedash.service.OpinionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OpinionController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class OpinionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OpinionService opinionService;

    @Test
    void saveOpinion_ShouldSaveOpinion() throws Exception {
        SaveOpinionDto opinionDto = SaveOpinionDto.builder()
                .userOpinion(UserOpinion.LIKE)
                .build();

        RecipeRatingDto ratingDto = RecipeRatingDto.builder()
                .recipeId("1")
                .recipeSource("Tasty")
                .likes(1L)
                .dislikes(0L)
                .neutral(0L)
                .userOpinion(UserOpinion.LIKE)
                .build();

        when(opinionService.saveOpinion(eq("Tasty"), eq("1"), any(SaveOpinionDto.class)))
                .thenReturn(ratingDto);

        mockMvc.perform(put("/opinions/Tasty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(opinionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipeId").value(ratingDto.getRecipeId()))
                .andExpect(jsonPath("$.recipeSource").value(ratingDto.getRecipeSource()))
                .andExpect(jsonPath("$.likes").value(ratingDto.getLikes()))
                .andExpect(jsonPath("$.dislikes").value(ratingDto.getDislikes()))
                .andExpect(jsonPath("$.neutral").value(ratingDto.getNeutral()))
                .andExpect(jsonPath("$.userOpinion").value("like"));
    }

    @Test
    void saveOpinion_shouldReturnBadRequest_WhenOpinionIsMissing() throws Exception {
        SaveOpinionDto invalidDto = SaveOpinionDto.builder()
                .build();

        mockMvc.perform(put("/opinions/Tasty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("userOpinion: User opinion is required"));
    }

    @Test
    void getRecipeRating_ShouldReturnRecipeRating() throws Exception {
        RecipeRatingDto ratingDto = RecipeRatingDto.builder()
                .recipeId("1")
                .recipeSource("Tasty")
                .likes(1L)
                .dislikes(2L)
                .neutral(0L)
                .userOpinion(UserOpinion.LIKE)
                .build();

        when(opinionService.getRecipeRating("1", "Tasty")).thenReturn(ratingDto);

        mockMvc.perform(get("/opinions/Tasty/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipeId").value(ratingDto.getRecipeId()))
                .andExpect(jsonPath("$.recipeSource").value(ratingDto.getRecipeSource()))
                .andExpect(jsonPath("$.likes").value(ratingDto.getLikes()))
                .andExpect(jsonPath("$.dislikes").value(ratingDto.getDislikes()))
                .andExpect(jsonPath("$.neutral").value(ratingDto.getNeutral()))
                .andExpect(jsonPath("$.userOpinion").value("like"));
    }
}
