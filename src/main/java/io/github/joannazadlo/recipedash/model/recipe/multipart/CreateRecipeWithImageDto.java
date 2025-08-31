package io.github.joannazadlo.recipedash.model.recipe.multipart;

import io.github.joannazadlo.recipedash.model.recipe.RecipeCreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CreateRecipeWithImageDto", description = "Multipart request containing recipe JSON and imagePath file")
public class CreateRecipeWithImageDto {

    @Schema(description = "Recipe JSON object", implementation = RecipeCreateDto.class)
    private RecipeCreateDto recipe;

    @Schema(description = "Image file", type = "string", format = "binary")
    private MultipartFile image;
}
