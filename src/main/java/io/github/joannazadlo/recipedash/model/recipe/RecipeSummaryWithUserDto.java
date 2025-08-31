package io.github.joannazadlo.recipedash.model.recipe;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RecipeSummaryWithUserDto extends RecipeSummaryDto {
    private String userEmail;
}
