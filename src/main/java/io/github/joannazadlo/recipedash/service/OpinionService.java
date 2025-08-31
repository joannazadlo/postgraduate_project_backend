package io.github.joannazadlo.recipedash.service;

import io.github.joannazadlo.recipedash.exception.user.UserNotFoundException;
import io.github.joannazadlo.recipedash.model.rating.SaveOpinionDto;
import io.github.joannazadlo.recipedash.model.rating.RecipeRatingDto;
import io.github.joannazadlo.recipedash.repository.OpinionRepository;
import io.github.joannazadlo.recipedash.repository.UserRepository;
import io.github.joannazadlo.recipedash.repository.entity.Opinion;
import io.github.joannazadlo.recipedash.repository.entity.User;
import io.github.joannazadlo.recipedash.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OpinionService {

    private final OpinionRepository opinionRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public RecipeRatingDto saveOpinion(String recipeSource, String recipeId, SaveOpinionDto opinion) {

        String uid = SecurityUtils.getCurrentUser().getUid();

        User user = userRepository.findById(uid)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Opinion opinionToSave = opinionRepository.findByUserUidAndRecipeIdAndRecipeSource(
                        uid, recipeId, recipeSource)
                .orElseGet(() -> Opinion.builder()
                        .user(user)
                        .recipeId(recipeId)
                        .recipeSource(recipeSource)
                        .build());

        opinionToSave.setUserOpinion(opinion.getUserOpinion());

        opinionRepository.save(opinionToSave);

        RecipeRatingDto rating = opinionRepository.aggregateRecipeRating(
                recipeId, recipeSource);

        rating.setUserOpinion(opinion.getUserOpinion());

        return rating;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public RecipeRatingDto deleteOpinion(String recipeId, String recipeSource) {
        String uid = SecurityUtils.getCurrentUser().getUid();

        opinionRepository.findByUserUidAndRecipeIdAndRecipeSource(uid, recipeId, recipeSource)
                .ifPresent(opinionRepository::delete);

        return Optional.ofNullable(
                opinionRepository.aggregateRecipeRating(recipeId, recipeSource)
        ).orElse(RecipeRatingDto.empty(recipeId, recipeSource));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public RecipeRatingDto getRecipeRating(String recipeId, String recipeSource) {

        String uid = SecurityUtils.getCurrentUser().getUid();

        final RecipeRatingDto rating = Optional.ofNullable(opinionRepository.aggregateRecipeRating(
                        recipeId, recipeSource))
                .orElse(RecipeRatingDto.empty(recipeId, recipeSource));

        opinionRepository.findByUserUidAndRecipeIdAndRecipeSource(
                uid,
                recipeId,
                recipeSource
        ).ifPresent(op -> rating.setUserOpinion(op.getUserOpinion()));

        return rating;
    }
}
