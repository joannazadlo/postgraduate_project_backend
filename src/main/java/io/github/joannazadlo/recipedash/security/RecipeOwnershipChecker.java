package io.github.joannazadlo.recipedash.security;

import io.github.joannazadlo.recipedash.repository.RecipeRepository;
import io.github.joannazadlo.recipedash.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("recipeOwnershipChecker")
@RequiredArgsConstructor
public class RecipeOwnershipChecker {

    private final RecipeRepository recipeRepository;

    public boolean isOwner(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .map(recipe -> {
                    if (recipe.getUser() == null) return false;
                    String currentUserId = SecurityUtils.getCurrentUser().getUid();
                    return recipe.getUser().getUid().equals(currentUserId);
                })
                .orElse(false);
    }
}
