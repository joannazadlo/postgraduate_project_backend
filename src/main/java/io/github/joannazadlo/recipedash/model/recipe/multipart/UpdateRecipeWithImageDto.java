package io.github.joannazadlo.recipedash.model.recipe.multipart;

import io.github.joannazadlo.recipedash.model.recipe.RecipeUpdateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "UpdateRecipeWithImageDto", description = "Multipart request containing recipe JSON and imagePath file")
public class UpdateRecipeWithImageDto {

    @Schema(description = "Recipe JSON object", implementation = RecipeUpdateDto.class)
    private RecipeUpdateDto recipe;

    @Schema(description = "Image file", type = "string", format = "binary")
    private MultipartFile image;
}
