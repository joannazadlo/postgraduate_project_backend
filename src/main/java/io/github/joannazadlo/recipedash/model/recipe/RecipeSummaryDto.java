package io.github.joannazadlo.recipedash.model.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RecipeSummaryDto {
    private Long id;
    private String title;
    private String imageSource;

    @JsonProperty("isPublic")
    private boolean publicRecipe;

    private LocalDateTime createdAt;
}
