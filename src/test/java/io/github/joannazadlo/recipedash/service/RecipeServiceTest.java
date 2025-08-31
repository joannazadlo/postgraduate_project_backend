package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.helper.RecipeHelper;
import io.github.joannazadlo.recipedash.mapper.RecipeMapper;
import io.github.joannazadlo.recipedash.model.enums.Role;
import io.github.joannazadlo.recipedash.model.enums.Status;
import io.github.joannazadlo.recipedash.model.recipe.RecipeCreateDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeDetailsDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeSummaryDto;
import io.github.joannazadlo.recipedash.model.recipeIngredient.RecipeIngredientCreateDto;
import io.github.joannazadlo.recipedash.model.recipeIngredient.RecipeIngredientDto;
import io.github.joannazadlo.recipedash.model.user.UserDto;
import io.github.joannazadlo.recipedash.repository.RecipeRepository;
import io.github.joannazadlo.recipedash.repository.UserRepository;
import io.github.joannazadlo.recipedash.repository.entity.Recipe;
import io.github.joannazadlo.recipedash.repository.entity.RecipeIngredient;
import io.github.joannazadlo.recipedash.repository.entity.User;
import io.github.joannazadlo.recipedash.utils.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeHelper recipeHelper;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private RecipeService recipeService;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    private UserDto userDto;

    private User user;

    @BeforeEach
    void setup() {
        userDto = UserDto.builder()
                .uid("user123")
                .email("test@test.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        user = User.builder()
                .uid("user123")
                .email("test@test.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getCurrentUser).thenReturn(userDto);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMock.close();
    }

    @Test
    void getRecipesForCurrentUser_shouldReturnMappedRecipes() {
        Recipe recipe = Recipe.builder()
                .id(1L)
                .title("Test Recipe")
                .imageSource("image.png")
                .publicRecipe(true)
                .createdAt(LocalDateTime.now())
                .build();

        List<Recipe> recipes = List.of(recipe);
        when(recipeRepository.findByUserUid(userDto.getUid())).thenReturn(recipes);

        RecipeSummaryDto summaryDto = RecipeSummaryDto.builder()
                .id(1L)
                .title("Test Recipe")
                .imageSource("image.png")
                .publicRecipe(true)
                .createdAt(recipe.getCreatedAt())
                .build();

        when(recipeMapper.toSummaryDto(recipe)).thenReturn(summaryDto);

        List<RecipeSummaryDto> result = recipeService.getRecipesForCurrentUser();

        assertEquals(1, result.size());

        RecipeSummaryDto resultDto = result.get(0);

        assertEquals(summaryDto.getId(), resultDto.getId());
        assertEquals(summaryDto.getTitle(), resultDto.getTitle());
        assertEquals(summaryDto.getImageSource(), resultDto.getImageSource());
        assertEquals(summaryDto.isPublicRecipe(), resultDto.isPublicRecipe());
        assertEquals(summaryDto.getCreatedAt(), resultDto.getCreatedAt());
    }

    @Test
    void getRecipesForCurrentUser_shouldReturnEmptyListWhenNoRecipes() {
        when(recipeRepository.findByUserUid(userDto.getUid())).thenReturn(List.of());

        List<RecipeSummaryDto> result = recipeService.getRecipesForCurrentUser();

        assertEquals(0, result.size());
    }

    @Test
    void createRecipe_shouldReturnRecipeDetailsDto() {
        RecipeCreateDto newRecipeDto = RecipeCreateDto.builder()
                .title("Test Recipe")
                .ingredients(List.of(RecipeIngredientCreateDto.builder()
                        .name("Rice")
                        .build()))
                .build();

        when(SecurityUtils.getCurrentUser()).thenReturn(userDto);
        when(userRepository.findById(userDto.getUid())).thenReturn(Optional.of(user));

        List<RecipeIngredient> ingredients = List.of(
                RecipeIngredient.builder().id(1L).name("Rice").build()
        );

        List<RecipeIngredientDto> ingredientsDto = List.of(
                RecipeIngredientDto.builder().id(1L).name("Rice").build()
        );

        Recipe preparedRecipe = Recipe.builder()
                .title(newRecipeDto.getTitle())
                .ingredients(ingredients)
                .user(user)
                .build();

        when(recipeHelper.prepareRecipeEntity(newRecipeDto, user, null)).thenReturn(preparedRecipe);

        Recipe savedRecipe = Recipe.builder()
                .id(1L)
                .title(preparedRecipe.getTitle())
                .ingredients(ingredients)
                .user(user)
                .build();

        when(recipeRepository.save(preparedRecipe)).thenReturn(savedRecipe);

        RecipeDetailsDto detailsDto = RecipeDetailsDto.builder()
                .id(savedRecipe.getId())
                .title(savedRecipe.getTitle())
                .ingredients(ingredientsDto)
                .build();

        when(recipeMapper.toDetailsDto(savedRecipe)).thenReturn(detailsDto);

        RecipeDetailsDto result = recipeService.createRecipe(newRecipeDto);

        assertEquals(detailsDto, result);

        verify(recipeMapper).toDetailsDto(savedRecipe);
    }

    @Test
    void createRecipeWithImage_shouldReturnRecipeDetailsDto() throws Exception {
        MultipartFile imageFile = mock(MultipartFile.class);

        RecipeCreateDto newRecipeDto = RecipeCreateDto.builder()
                .title("Test Recipe")
                .ingredients(List.of(
                        RecipeIngredientCreateDto.builder().name("Rice").build()
                ))
                .build();

        when(SecurityUtils.getCurrentUser()).thenReturn(userDto);
        when(userRepository.findById(userDto.getUid())).thenReturn(Optional.of(user));

        String savedImageSource = "image.png";

        when(imageService.saveImage(imageFile)).thenReturn(savedImageSource);

        List<RecipeIngredient> ingredients = List.of(
                RecipeIngredient.builder().id(1L).name("Rice").build()
        );

        List<RecipeIngredientDto> ingredientsDto = List.of(
                RecipeIngredientDto.builder().id(1L).name("Rice").build()
        );

        Recipe preparedRecipe = Recipe.builder()
                .title(newRecipeDto.getTitle())
                .ingredients(ingredients)
                .user(user)
                .imageSource(savedImageSource)
                .build();

        Recipe savedRecipe = Recipe.builder()
                .id(1L)
                .title(preparedRecipe.getTitle())
                .ingredients(ingredients)
                .user(user)
                .imageSource(savedImageSource)
                .build();

        when(recipeHelper.prepareRecipeEntity(newRecipeDto, user, savedImageSource)).thenReturn(preparedRecipe);

        when(recipeRepository.save(preparedRecipe)).thenReturn(savedRecipe);

        RecipeDetailsDto detailsDto = RecipeDetailsDto.builder()
                .id(savedRecipe.getId())
                .title(savedRecipe.getTitle())
                .ingredients(ingredientsDto)
                .imageSource(savedRecipe.getImageSource())
                .build();

        when(recipeMapper.toDetailsDto(savedRecipe)).thenReturn(detailsDto);

        RecipeDetailsDto result = recipeService.createRecipeWithImage(newRecipeDto, imageFile);

        assertEquals(detailsDto, result);

        verify(imageService, times(1)).saveImage(imageFile);
        verify(recipeHelper).prepareRecipeEntity(newRecipeDto, user, savedImageSource);
        verify(recipeRepository).save(preparedRecipe);
        verify(recipeMapper).toDetailsDto(savedRecipe);
    }
}

