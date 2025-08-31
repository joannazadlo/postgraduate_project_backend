package io.github.joannazadlo.recipedash.controller;

import io.github.joannazadlo.recipedash.exception.userIngredient.UserIngredientNotFoundException;
import io.github.joannazadlo.recipedash.model.userIngredient.UserIngredientCreateDto;
import io.github.joannazadlo.recipedash.model.userIngredient.UserIngredientDto;
import io.github.joannazadlo.recipedash.service.UserIngredientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserIngredientController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class UserIngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserIngredientService userIngredientService;

    @Test
    void createUserIngredient_ShouldReturnCreatedIngredient() throws Exception {
        UserIngredientCreateDto createDto = UserIngredientCreateDto.builder()
                .ingredient("Chicken")
                .build();

        UserIngredientDto returnedDto = UserIngredientDto.builder()
                .id(1L)
                .ingredient("Chicken")
                .build();

        when(userIngredientService.createUserIngredient(any())).thenReturn(returnedDto);

        mockMvc.perform(post("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(returnedDto.getId()))
                .andExpect(jsonPath("$.ingredient").value("Chicken"));
    }

    @Test
    void createUserIngredient_shouldReturnBadRequest_whenIngredientIsBlank() throws Exception {
        UserIngredientCreateDto invalidDto = UserIngredientCreateDto.builder()
                .ingredient("")
                .build();

        mockMvc.perform(post("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserIngredients_ShouldReturnListOfUserIngredients() throws Exception {
        List<UserIngredientDto> mockIngredients = List.of(
                new UserIngredientDto(1L, "Chicken"),
                new UserIngredientDto(2L, "Tomato")
        );

        when(userIngredientService.getUserIngredients()).thenReturn(mockIngredients);

        mockMvc.perform(get("/ingredients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].ingredient").value("Chicken"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].ingredient").value("Tomato"));
    }

    @Test
    void getUserIngredients_shouldReturnEmptyList_whenNoIngredients() throws Exception {
        when(userIngredientService.getUserIngredients()).thenReturn(List.of());

        mockMvc.perform(get("/ingredients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void deleteUserIngredient_shouldDeleteUserIngredientAndReturnNoContent() throws Exception {
        Long idToDelete = 1L;

        doNothing().when(userIngredientService).deleteUserIngredient(idToDelete);

        mockMvc.perform(delete("/ingredients/{id}", idToDelete))
                .andExpect(status().isNoContent());

        verify(userIngredientService).deleteUserIngredient(idToDelete);
    }

    @Test
    void deleteUserIngredient_shouldReturnNotFound_whenIngredientDoesNotExist() throws Exception {
        Long nonExistentId = 999L;

        doThrow(new UserIngredientNotFoundException("Ingredient not found: " + nonExistentId))
                .when(userIngredientService).deleteUserIngredient(nonExistentId);

        mockMvc.perform(delete("/ingredients/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("Ingredient not found: " + nonExistentId));
    }
}
