package io.github.joannazadlo.recipedash.controller;

import io.github.joannazadlo.recipedash.exception.image.InvalidFileFormatException;
import io.github.joannazadlo.recipedash.exception.recipe.RecipeNotFoundException;
import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.recipeIngredient.RecipeIngredientCreateDto;
import io.github.joannazadlo.recipedash.model.recipeIngredient.RecipeIngredientDto;
import io.github.joannazadlo.recipedash.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.joannazadlo.recipedash.model.recipe.RecipeCreateDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeDetailsDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeSummaryDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeUpdateDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(RecipeController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RecipeService recipeService;

    @Captor
    private ArgumentCaptor<MultipartFile> imageCaptor;

    @Test
    void getRecipes_withoutCriteria_shouldReturnUserRecipes() throws Exception {
        List<RecipeSummaryDto> mockRecipes = List.of(
                RecipeSummaryDto.builder()
                        .id(1L)
                        .title("Chicken with rice")
                        .imageSource("https://images.example.com/recipes/chicken-rice.jpg")
                        .publicRecipe(true)
                        .createdAt(LocalDateTime.of(2025, 7, 18, 12, 0))
                        .build(),
                RecipeSummaryDto.builder()
                        .id(2L)
                        .title("Pasta")
                        .imageSource("https://images.example.com/recipes/pasta.jpg")
                        .publicRecipe(false)
                        .createdAt(LocalDateTime.of(2025, 7, 17, 8, 30))
                        .build()
        );

        when(recipeService.getRecipesForCurrentUser()).thenReturn(mockRecipes);

        mockMvc.perform(get("/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Chicken with rice"))
                .andExpect(jsonPath("$[0].imageSource").value("https://images.example.com/recipes/chicken-rice.jpg"))
                .andExpect(jsonPath("$[0].isPublic").value(true))
                .andExpect(jsonPath("$[0].createdAt").value("2025-07-18T12:00:00"))

                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Pasta"))
                .andExpect(jsonPath("$[1].imageSource").value("https://images.example.com/recipes/pasta.jpg"))
                .andExpect(jsonPath("$[1].isPublic").value(false))
                .andExpect(jsonPath("$[1].createdAt").value("2025-07-17T08:30:00"));
    }

    @Test
    void getRecipes_withoutCriteriaAndNoRecipesExist_shouldReturnEmptyList() throws Exception {
        when(recipeService.getRecipesForCurrentUser()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRecipes_withInvalidCuisine_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/recipes")
                        .param("cuisine", "MEXICAN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value(Matchers.containsString("Failed to convert property value")));
    }

    @Test
    void getRecipes_withInvalidDietaryPreference_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/recipes")
                        .param("dietaryPreferences", "UNKNOWN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRecipeById_ShouldReturnRecipe() throws Exception {
        RecipeDetailsDto recipeDto = RecipeDetailsDto.builder()
                .id(1L)
                .title("Chicken with rice")
                .imageSource("https://images.example.com/recipes/chicken-rice.jpg")
                .ingredients(List.of(RecipeIngredientDto.builder()
                                .name("chicken")
                                .build(),
                        RecipeIngredientDto.builder()
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

        when(recipeService.getRecipeById(1L)).thenReturn(recipeDto);

        mockMvc.perform(get("/recipes/1")
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
    void getRecipeById_ShouldReturnNotFound_WhenRecipeDoesNotExist() throws Exception {
        Long nonExistentId = 1223L;

        doThrow(new RecipeNotFoundException("Recipe not found: " + nonExistentId))
                .when(recipeService).getRecipeById(nonExistentId);

        mockMvc.perform(get("/recipes/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("Recipe not found: " + nonExistentId));
    }

    @Test
    void createRecipe_WithRequiredFieldsOnly_ShouldReturnCreatedRecipe() throws Exception {
        RecipeCreateDto createDto = RecipeCreateDto.builder()
                .title("Chicken with rice")
                .ingredients(List.of(RecipeIngredientCreateDto.builder()
                                .name("chicken")
                                .build(),
                        RecipeIngredientCreateDto.builder()
                                .name("rice")
                                .quantity("150g")
                                .build()))
                .build();

        RecipeDetailsDto recipeDto = RecipeDetailsDto.builder()
                .id(1L)
                .title("Chicken with rice")
                .ingredients(List.of(
                        RecipeIngredientDto.builder().id(10L).name("chicken").build(),
                        RecipeIngredientDto.builder().id(11L).name("rice").quantity("150g").build()
                ))
                .steps(Collections.emptyList())
                .cuisine(null)
                .dietaryPreferences(Collections.emptyList())
                .cookingTime("")
                .publicRecipe(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(recipeService.createRecipe(any())).thenReturn(recipeDto);

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Chicken with rice"))
                .andExpect(jsonPath("$.ingredients[0].id").value(10L))
                .andExpect(jsonPath("$.ingredients[0].name").value("chicken"))
                .andExpect(jsonPath("$.ingredients[1].id").value(11L))
                .andExpect(jsonPath("$.ingredients[1].name").value("rice"))
                .andExpect(jsonPath("$.ingredients[1].quantity").value("150g"))
                .andExpect(jsonPath("$.steps").isEmpty())
                .andExpect(jsonPath("$.cuisine").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.dietaryPreferences").isEmpty())
                .andExpect(jsonPath("$.cookingTime").value(""))
                .andExpect(jsonPath("$.isPublic").value(false))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void createRecipe_WithAllFieldsSet_ShouldReturnCreatedRecipe() throws Exception {
        RecipeCreateDto createDto = RecipeCreateDto.builder()
                .title("Vegan Salad")
                .ingredients(List.of(
                        RecipeIngredientCreateDto.builder().name("lettuce").quantity("100g").build(),
                        RecipeIngredientCreateDto.builder().name("tomato").quantity("50g").build()
                ))
                .steps(List.of("Chop vegetables", "Mix everything"))
                .dietaryPreferences(List.of(DietaryPreferenceType.VEGAN, DietaryPreferenceType.GLUTEN_FREE))
                .cookingTime("15 minutes")
                .cuisine(CuisineType.EUROPEAN)
                .publicRecipe(true)
                .build();

        RecipeDetailsDto recipeDto = RecipeDetailsDto.builder()
                .id(2L)
                .title("Vegan Salad")
                .ingredients(List.of(
                        RecipeIngredientDto.builder().id(20L).name("lettuce").quantity("100g").build(),
                        RecipeIngredientDto.builder().id(21L).name("tomato").quantity("50g").build()
                ))
                .steps(List.of("Chop vegetables", "Mix everything"))
                .dietaryPreferences(List.of(DietaryPreferenceType.VEGAN, DietaryPreferenceType.GLUTEN_FREE))
                .cookingTime("15 minutes")
                .cuisine(CuisineType.EUROPEAN)
                .publicRecipe(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(recipeService.createRecipe(any())).thenReturn(recipeDto);

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.title").value("Vegan Salad"))
                .andExpect(jsonPath("$.ingredients[0].id").value(20L))
                .andExpect(jsonPath("$.ingredients[0].name").value("lettuce"))
                .andExpect(jsonPath("$.ingredients[0].quantity").value("100g"))
                .andExpect(jsonPath("$.ingredients[1].id").value(21L))
                .andExpect(jsonPath("$.ingredients[1].name").value("tomato"))
                .andExpect(jsonPath("$.ingredients[1].quantity").value("50g"))
                .andExpect(jsonPath("$.steps[0]").value("Chop vegetables"))
                .andExpect(jsonPath("$.steps[1]").value("Mix everything"))
                .andExpect(jsonPath("$.dietaryPreferences").isArray())
                .andExpect(jsonPath("$.dietaryPreferences", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.dietaryPreferences", Matchers.hasItems("Vegan", "Gluten-Free")))
                .andExpect(jsonPath("$.cookingTime").value("15 minutes"))
                .andExpect(jsonPath("$.cuisine").value("European"))
                .andExpect(jsonPath("$.isPublic").value(true))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void createRecipe_MissingTitle_ShouldReturnBadRequest() throws Exception {
        RecipeCreateDto createDto = RecipeCreateDto.builder()
                .ingredients(List.of(RecipeIngredientCreateDto.builder().name("chicken").build()))
                .build();

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", Matchers.hasItem("title: Recipe title is required")));
    }

    @Test
    void createRecipeWithImage_ShouldReturnCreatedRecipe() throws Exception {
        RecipeCreateDto createDto = RecipeCreateDto.builder()
                .title("Chicken with rice")
                .ingredients(List.of(RecipeIngredientCreateDto.builder()
                                .name("chicken")
                                .build(),
                        RecipeIngredientCreateDto.builder()
                                .name("rice")
                                .quantity("150g")
                                .build()))
                .build();

        RecipeDetailsDto recipeDto = RecipeDetailsDto.builder()
                .id(1L)
                .title("Chicken with rice")
                .imageSource("http://example.com/images/test-image.jpg")
                .ingredients(List.of(
                        RecipeIngredientDto.builder().id(10L).name("chicken").build(),
                        RecipeIngredientDto.builder().id(11L).name("rice").quantity("150g").build()
                ))
                .steps(Collections.emptyList())
                .cuisine(null)
                .dietaryPreferences(Collections.emptyList())
                .cookingTime("")
                .publicRecipe(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(recipeService.createRecipeWithImage(any(RecipeCreateDto.class), any(MultipartFile.class)))
                .thenReturn(recipeDto);

        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        MockMultipartFile recipePart = new MockMultipartFile(
                "recipe",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(createDto)
        );

        mockMvc.perform(multipart("/recipes/image")
                        .file(imageFile)
                        .file(recipePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Chicken with rice"))
                .andExpect(jsonPath("$.imageSource").value("http://example.com/images/test-image.jpg"))
                .andExpect(jsonPath("$.ingredients[0].id").value(10L))
                .andExpect(jsonPath("$.ingredients[0].name").value("chicken"))
                .andExpect(jsonPath("$.ingredients[1].id").value(11L))
                .andExpect(jsonPath("$.ingredients[1].name").value("rice"))
                .andExpect(jsonPath("$.ingredients[1].quantity").value("150g"))
                .andExpect(jsonPath("$.steps").isEmpty())
                .andExpect(jsonPath("$.cuisine").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.dietaryPreferences").isEmpty())
                .andExpect(jsonPath("$.cookingTime").value(""))
                .andExpect(jsonPath("$.isPublic").value(false))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(recipeService).createRecipeWithImage(any(), imageCaptor.capture());
        MultipartFile capturedImage = imageCaptor.getValue();

        assertEquals("test-image.jpg", capturedImage.getOriginalFilename());
        assertEquals("test image content", new String(capturedImage.getBytes()));
    }

    @Test
    void createRecipeWithImage_InvalidMimeType_ShouldReturnUnsupportedMediaType() throws Exception {
        RecipeCreateDto createDto = RecipeCreateDto.builder()
                .title("Chicken with rice")
                .ingredients(List.of(RecipeIngredientCreateDto.builder()
                                .name("chicken")
                                .build(),
                        RecipeIngredientCreateDto.builder()
                                .name("rice")
                                .quantity("150g")
                                .build()))
                .build();

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "bad-file.exe", "application/octet-stream", "dummy".getBytes()
        );

        MockMultipartFile recipePart = new MockMultipartFile(
                "recipe", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(createDto)
        );

        when(recipeService.createRecipeWithImage(any(), any()))
                .thenThrow(new InvalidFileFormatException("Invalid file format"));

        mockMvc.perform(multipart("/recipes/image")
                        .file(recipePart)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.error").value("UNSUPPORTED_MEDIA_TYPE"))
                .andExpect(jsonPath("$.errorMessage").value("Invalid file format"));
    }

    @Test
    void updateRecipe_ShouldReturnUpdatedRecipe() throws Exception {
        Long recipeId = 1L;

        RecipeUpdateDto updateDto = RecipeUpdateDto.builder()
                .title("Updated title")
                .build();

        RecipeDetailsDto expectedDto = RecipeDetailsDto.builder()
                .id(recipeId)
                .title("Updated title")
                .build();

        when(recipeService.updateRecipe(eq(recipeId), any(RecipeUpdateDto.class)))
                .thenReturn(expectedDto);

        mockMvc.perform(put("/recipes/{id}", recipeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recipeId))
                .andExpect(jsonPath("$.title").value("Updated title"));
    }

    @Test
    void updateRecipe_MissingTitle_ShouldReturnBadRequest() throws Exception {
        Long recipeId = 1L;

        RecipeUpdateDto updateDto = RecipeUpdateDto.builder()
                .title("")
                .build();

        mockMvc.perform(put("/recipes/{id}", recipeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", Matchers.hasItem("title: Recipe title is required")));
    }

    @Test
    void updateRecipe_ShouldReturnNotFound_WhenRecipeDoesNotExist() throws Exception {
        Long nonExistentId = 1223L;

        RecipeUpdateDto dto = RecipeUpdateDto.builder()
                .title("Some title")
                .build();

        when(recipeService.updateRecipe(eq(nonExistentId), any(RecipeUpdateDto.class)))
                .thenThrow(new RecipeNotFoundException("Recipe not found: " + nonExistentId));

        mockMvc.perform(put("/recipes/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("Recipe not found: " + nonExistentId));
    }

    @Test
    void updateRecipeWithImage_ShouldReturnUpdatedRecipe() throws Exception {
        Long recipeId = 1223L;

        RecipeUpdateDto updateDto = RecipeUpdateDto.builder()
                .title("Chicken with rice")
                .build();

        RecipeDetailsDto recipeDto = RecipeDetailsDto.builder()
                .id(1L)
                .title("Chicken with rice")
                .imageSource("http://example.com/images/test-image.jpg")
                .ingredients(List.of())
                .steps(Collections.emptyList())
                .cuisine(null)
                .dietaryPreferences(Collections.emptyList())
                .cookingTime("")
                .publicRecipe(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(recipeService.updateRecipeWithImage(eq(recipeId), any(RecipeUpdateDto.class), any(MultipartFile.class), eq(false)))
                .thenReturn(recipeDto);

        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        MockMultipartFile recipePart = new MockMultipartFile(
                "recipe",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(updateDto)
        );

        mockMvc.perform(multipart("/recipes/{id}/image", recipeId)
                        .file(imageFile)
                        .file(recipePart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Chicken with rice"))
                .andExpect(jsonPath("$.imageSource").value("http://example.com/images/test-image.jpg"));

        verify(recipeService).updateRecipeWithImage(anyLong(), any(), imageCaptor.capture(), eq(false));
        MultipartFile capturedImage = imageCaptor.getValue();

        assertEquals("test-image.jpg", capturedImage.getOriginalFilename());
        assertEquals("test image content", new String(capturedImage.getBytes()));
    }

    @Test
    void deleteRecipe_shouldDeleteRecipeAndReturnNoContent() throws Exception {
        Long idToDelete = 1L;

        doNothing().when(recipeService).deleteRecipe(idToDelete);

        mockMvc.perform(delete("/recipes/{id}", idToDelete))
                .andExpect(status().isNoContent());

        verify(recipeService).deleteRecipe(idToDelete);
    }

    @Test
    void deleteRecipe_shouldReturnNotFound_whenRecipeDoesNotExist() throws Exception {
        Long nonExistentId = 999L;

        doThrow(new RecipeNotFoundException("Recipe not found: " + nonExistentId))
                .when(recipeService).deleteRecipe(nonExistentId);

        mockMvc.perform(delete("/recipes/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("Recipe not found: " + nonExistentId));
    }

    @Test
    void getFilteredRecipes_ShouldReturnFilteredResults() throws Exception {
        RecipeSummaryDto dto = RecipeSummaryDto.builder()
                .id(1L)
                .title("Vegan Tacos")
                .imageSource("https://images.example.com/recipes/tacos.jpg")
                .build();

        when(recipeService.searchRecipesForUser(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/recipes")
                        .param("ingredients", "avocado", "beans")
                        .param("cuisine", "ASIAN")
                        .param("dietaryPreferences", "VEGAN", "GLUTEN_FREE")
                        .param("isPublic", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Vegan Tacos"))
                .andExpect(jsonPath("$[0].imageSource").value("https://images.example.com/recipes/tacos.jpg"));
    }
}
