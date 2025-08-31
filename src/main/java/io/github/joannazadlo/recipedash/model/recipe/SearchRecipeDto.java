package io.github.joannazadlo.recipedash.model.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRecipeDto {
    private String id;
    private String title;
    private String imageSource;
    private String source;
}
