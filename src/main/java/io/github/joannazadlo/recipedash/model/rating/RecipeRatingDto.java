package io.github.joannazadlo.recipedash.model.rating;

import io.github.joannazadlo.recipedash.model.enums.UserOpinion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeRatingDto {
    private String recipeId;
    private String recipeSource;
    private Long likes;
    private Long dislikes;
    private Long neutral;
    private UserOpinion userOpinion;

    public RecipeRatingDto(String recipeId, String recipeSource, Long likes, Long dislikes, Long neutral) {
        this.recipeId = recipeId;
        this.recipeSource = recipeSource;
        this.likes = likes;
        this.dislikes = dislikes;
        this.neutral = neutral;
    }

    public static RecipeRatingDto empty(String recipeId, String recipeSource) {
        return RecipeRatingDto.builder()
                .recipeId(recipeId)
                .recipeSource(recipeSource)
                .likes(0L)
                .dislikes(0L)
                .neutral(0L)
                .build();
    }
}
