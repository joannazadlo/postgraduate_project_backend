package io.github.joannazadlo.recipedash.repository;

import io.github.joannazadlo.recipedash.model.rating.RecipeRatingDto;
import io.github.joannazadlo.recipedash.repository.entity.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpinionRepository extends JpaRepository<Opinion, Long> {

    Optional<Opinion> findByUserUidAndRecipeIdAndRecipeSource(String userUid, String recipeId, String recipeSource);

    @Query("""
                SELECT new io.github.joannazadlo.recipedash.model.rating.RecipeRatingDto(
                    o.recipeId,
                    o.recipeSource,
                    SUM(CASE WHEN o.userOpinion = io.github.joannazadlo.recipedash.model.enums.UserOpinion.LIKE THEN 1 ELSE 0 END),
                    SUM(CASE WHEN o.userOpinion = io.github.joannazadlo.recipedash.model.enums.UserOpinion.DISLIKE THEN 1 ELSE 0 END),
                    SUM(CASE WHEN o.userOpinion = io.github.joannazadlo.recipedash.model.enums.UserOpinion.NEUTRAL THEN 1 ELSE 0 END)
                )
                FROM Opinion o
                WHERE o.recipeId = :recipeId AND o.recipeSource = :recipeSource
                GROUP BY o.recipeId, o.recipeSource
            """)
    RecipeRatingDto aggregateRecipeRating(
            @Param("recipeId") String recipeId,
            @Param("recipeSource") String recipeSource
    );


    @Query("""
                SELECT CONCAT(o.recipeId, '|', o.recipeSource)
                FROM Opinion o
                WHERE o.user.uid = :userId AND o.userOpinion = io.github.joannazadlo.recipedash.model.enums.UserOpinion.DISLIKE
            """)
    List<String> findDislikedRecipeKeysByUser(@Param("userId") String userId);

}
