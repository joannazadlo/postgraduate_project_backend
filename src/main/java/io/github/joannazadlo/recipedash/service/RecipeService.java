package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.exception.recipe.RecipeNotFoundException;
import io.github.joannazadlo.recipedash.exception.user.UserNotFoundException;
import io.github.joannazadlo.recipedash.mapper.RecipeMapper;
import io.github.joannazadlo.recipedash.model.recipe.RecipeCreateDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeDetailsDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeSummaryDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeUpdateDto;
import io.github.joannazadlo.recipedash.model.searchCriteria.RecipeSearchCriteriaDto;
import io.github.joannazadlo.recipedash.repository.entity.Recipe;
import io.github.joannazadlo.recipedash.model.user.UserDto;
import io.github.joannazadlo.recipedash.repository.RecipeRepository;
import io.github.joannazadlo.recipedash.repository.UserRepository;
import io.github.joannazadlo.recipedash.helper.RecipeHelper;
import io.github.joannazadlo.recipedash.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import io.github.joannazadlo.recipedash.repository.entity.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeMapper recipeMapper;
    private final ImageService imageService;
    private final RecipeHelper recipeUtils;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<RecipeSummaryDto> getRecipesForCurrentUser() {
        UserDto currentUser = SecurityUtils.getCurrentUser();

        List<Recipe> recipes = recipeRepository.findByUserUid(currentUser.getUid());

        return recipes.stream()
                .map(recipeMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public RecipeDetailsDto getRecipeById(Long id) {
        return recipeRepository.findById(id).map(recipeMapper::toDetailsDto)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found: " + id));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Transactional
    public RecipeDetailsDto createRecipe(RecipeCreateDto newRecipeDto) {
        String userId = SecurityUtils.getCurrentUser().getUid();

        return saveRecipeEntity(newRecipeDto, userId);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Transactional
    public RecipeDetailsDto createRecipeWithImage(RecipeCreateDto newRecipeDto, MultipartFile imageFile) {

        String userId = SecurityUtils.getCurrentUser().getUid();

        String imageSource = imageService.saveImage(imageFile);

        return saveRecipeEntity(newRecipeDto, userId, imageSource);
    }

    private RecipeDetailsDto saveRecipeEntity(RecipeCreateDto newRecipeDto, String userId) {
        return saveRecipeEntity(newRecipeDto, userId, null);
    }

    private RecipeDetailsDto saveRecipeEntity(RecipeCreateDto newRecipeDto, String userId, String imageSource) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Recipe newRecipe = recipeUtils.prepareRecipeEntity(newRecipeDto, user, imageSource);
        Recipe savedRecipe = recipeRepository.save(newRecipe);
        return recipeMapper.toDetailsDto(savedRecipe);
    }

    @PreAuthorize("hasRole('ADMIN') or @recipeOwnershipChecker.isOwner(#id)")
    @Transactional
    public RecipeDetailsDto updateRecipe(Long id, RecipeUpdateDto updatedRecipeDto) {
        Recipe recipeToUpdate = recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found: " + id));

        recipeUtils.updateRecipeFieldsFromDto(updatedRecipeDto, recipeToUpdate);

        Recipe saved = recipeRepository.save(recipeToUpdate);

        return recipeMapper.toDetailsDto(saved);
    }

    @PreAuthorize("hasRole('ADMIN') or @recipeOwnershipChecker.isOwner(#id)")
    @Transactional
    public RecipeDetailsDto updateRecipeWithImage(Long id, RecipeUpdateDto updatedRecipeDto, MultipartFile imageFile, boolean imageRemoved) {

        Recipe recipeToUpdate = recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found: " + id));

        imageService.handleImageUpdate(imageFile, imageRemoved, recipeToUpdate);

        recipeUtils.updateRecipeFieldsFromDto(updatedRecipeDto, recipeToUpdate);

        Recipe saved = recipeRepository.save(recipeToUpdate);
        return recipeMapper.toDetailsDto(saved);
    }

    @PreAuthorize("hasRole('ADMIN') or @recipeOwnershipChecker.isOwner(#id)")
    @Transactional
    public void deleteRecipe(Long id) {
        Recipe recipeToDelete = recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found: " + id));

        String imageSource = recipeToDelete.getImageSource();

        if (imageSource != null && !imageSource.isEmpty()) {
            imageService.deleteImage(imageSource);
        }

        recipeRepository.deleteById(id);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<RecipeSummaryDto> searchRecipesForUser(RecipeSearchCriteriaDto criteria) {
        UserDto currentUser = SecurityUtils.getCurrentUser();

        return recipeRepository.findByUserUid(currentUser.getUid()).stream()
                .filter(recipeUtils.recipeMatchesSearchCriteria(
                        criteria.getIngredients(),
                        criteria.getCuisine(),
                        criteria.getDietaryPreferences()
                ))
                .filter(recipeUtils.filterByPublicStatus(criteria.getIsPublic()))
                .map(recipeMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<RecipeSummaryDto> searchPublicRecipes(RecipeSearchCriteriaDto criteria) {
        return recipeRepository.findAll().stream()
                .filter(Recipe::isPublicRecipe)
                .filter(recipeUtils.recipeMatchesSearchCriteria(
                        criteria.getIngredients(),
                        criteria.getCuisine(),
                        criteria.getDietaryPreferences()))
                .map(recipeMapper::toSummaryDto)
                .collect(Collectors.toList());
    }
}
