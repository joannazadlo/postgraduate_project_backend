package io.github.joannazadlo.recipedash.model.externalRecipe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ExternalRecipeSummaryDto {
    private String id;
    private String title;
    private String imageSource;
}
