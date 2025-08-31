package io.github.joannazadlo.recipedash.helper;

import io.github.joannazadlo.recipedash.mapper.IngredientMapper;
import io.github.joannazadlo.recipedash.mapper.RecipeMapper;
import io.github.joannazadlo.recipedash.mapper.UserMapper;
import io.github.joannazadlo.recipedash.model.enums.CuisineType;
import io.github.joannazadlo.recipedash.model.enums.DietaryPreferenceType;
import io.github.joannazadlo.recipedash.model.recipe.RecipeCreateDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeSummaryWithUserDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeUpdateDto;
import io.github.joannazadlo.recipedash.model.user.UserDto;
import io.github.joannazadlo.recipedash.repository.entity.RecipeIngredient;
import io.github.joannazadlo.recipedash.repository.entity.Recipe;
import io.github.joannazadlo.recipedash.repository.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class RecipeHelper {

    private final UserMapper userMapper;
    private final IngredientMapper ingredientMapper;
    private final RecipeMapper recipeMapper;

    public Recipe prepareRecipeEntity(RecipeCreateDto dto, User user, String imageSource) {
        Recipe recipe = recipeMapper.toEntity(dto);

        recipe.setUser(user);

        if (imageSource != null) {
            recipe.setImageSource(imageSource);
        }

        if (recipe.getIngredients() != null) {
            recipe.getIngredients().forEach(i -> i.setRecipe(recipe));
        }

        return recipe;
    }

    public void updateRecipeFieldsFromDto(RecipeUpdateDto updatedRecipeDto, Recipe recipeToUpdate) {
        recipeToUpdate.setTitle(updatedRecipeDto.getTitle());

        recipeToUpdate.getSteps().clear();
        if (updatedRecipeDto.getSteps() != null) {
            recipeToUpdate.getSteps().addAll(updatedRecipeDto.getSteps());
        }

        if (updatedRecipeDto.getIngredients() != null) {
            recipeToUpdate.getIngredients().clear();
            updatedRecipeDto.getIngredients().forEach(ingredientDto -> {
                RecipeIngredient ingredient = ingredientMapper.toUpdateEntity(ingredientDto);
                ingredient.setRecipe(recipeToUpdate);
                recipeToUpdate.getIngredients().add(ingredient);
            });
        }

        recipeToUpdate.setPublicRecipe(updatedRecipeDto.isPublicRecipe());
        recipeToUpdate.setCuisine(updatedRecipeDto.getCuisine());
        recipeToUpdate.setCookingTime(updatedRecipeDto.getCookingTime());

        recipeToUpdate.getDietaryPreferences().clear();
        if (updatedRecipeDto.getDietaryPreferences() != null) {
            recipeToUpdate.getDietaryPreferences().addAll(updatedRecipeDto.getDietaryPreferences());
        }
    }

    public Predicate<Recipe> recipeMatchesSearchCriteria(
            List<String> ingredients,
            CuisineType cuisine,
            List<DietaryPreferenceType> dietaryPreferences
    ) {
        return recipe -> {
            boolean matchesIngredients = ingredients == null || ingredients.isEmpty() ||
                    (
                            recipe.getIngredients() != null &&
                                    ingredients.stream()
                                            .map(i -> i.toLowerCase().trim())
                                            .allMatch(filterIng ->
                                                    recipe.getIngredients().stream()
                                                            .map(ing -> ing.getName().toLowerCase().trim())
                                                            .anyMatch(recipeIng -> recipeIng.equals(filterIng))
                                            )
                    );

            boolean matchesCuisine = cuisine == null ||
                    (recipe.getCuisine() != null && recipe.getCuisine() == cuisine);

            boolean matchesDietary = dietaryPreferences == null || dietaryPreferences.isEmpty() ||
                    (recipe.getDietaryPreferences() != null &&
                            dietaryPreferences.stream()
                                    .allMatch(recipe.getDietaryPreferences()::contains)
                    );

            return matchesIngredients && matchesCuisine && matchesDietary;
        };
    }

    public Predicate<Recipe> filterByPublicStatus(Boolean isPublic) {
        return recipe -> isPublic == null || recipe.isPublicRecipe() == isPublic;
    }

    public RecipeSummaryWithUserDto mapRecipeWithUserDto(
            Recipe recipe,
            User user
    ) {
        UserDto userDto = userMapper.toDto(user);

        RecipeSummaryWithUserDto dto = new RecipeSummaryWithUserDto();
        dto.setId(recipe.getId());
        dto.setTitle(recipe.getTitle());
        dto.setImageSource(recipe.getImageSource());
        dto.setPublicRecipe(recipe.isPublicRecipe());
        dto.setCreatedAt(recipe.getCreatedAt());
        if (userDto != null && userDto.getEmail() != null) {
            dto.setUserEmail(userDto.getEmail());
        }
        return dto;
    }
}
