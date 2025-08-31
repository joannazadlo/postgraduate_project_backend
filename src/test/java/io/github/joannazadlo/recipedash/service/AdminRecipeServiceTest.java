package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.helper.RecipeHelper;
import io.github.joannazadlo.recipedash.model.enums.Role;
import io.github.joannazadlo.recipedash.model.enums.Status;
import io.github.joannazadlo.recipedash.model.recipe.RecipeSummaryWithUserDto;
import io.github.joannazadlo.recipedash.model.user.UserDto;
import io.github.joannazadlo.recipedash.repository.RecipeRepository;
import io.github.joannazadlo.recipedash.repository.entity.Recipe;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminRecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeHelper recipeHelper;

    @InjectMocks
    private AdminRecipeService adminRecipeService;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    private User user;

    @BeforeEach
    void setup() {
        UserDto userDto = UserDto.builder()
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
    void getAllRecipes_shouldReturnMappedRecipes() {
        Recipe recipe = Recipe.builder()
                .id(1L)
                .title("Test Recipe")
                .imageSource("image.png")
                .publicRecipe(true)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        List<Recipe> recipes = List.of(recipe);
        when(recipeRepository.findAllWithUser()).thenReturn(recipes);

        RecipeSummaryWithUserDto summaryDto = RecipeSummaryWithUserDto.builder()
                .id(1L)
                .title("Test Recipe")
                .imageSource("image.png")
                .publicRecipe(true)
                .createdAt(recipe.getCreatedAt())
                .userEmail("test@test.com")
                .build();

        when(recipeHelper.mapRecipeWithUserDto(recipe, user)).thenReturn(summaryDto);

        List<RecipeSummaryWithUserDto> result = adminRecipeService.getAllRecipes();

        assertEquals(1, result.size());

        RecipeSummaryWithUserDto resultDto = result.get(0);

        assertEquals(List.of(summaryDto), result);
    }

    @Test
    void getAllRecipes_shouldReturnEmptyListWhenNoRecipes() {
        when(recipeRepository.findAllWithUser()).thenReturn(List.of());

        List<RecipeSummaryWithUserDto> result = adminRecipeService.getAllRecipes();

        assertEquals(0, result.size());
    }
}
