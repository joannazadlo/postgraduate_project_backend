package io.github.joannazadlo.recipedash.mapper;

import io.github.joannazadlo.recipedash.model.recipe.RecipeCreateDto;
import io.github.joannazadlo.recipedash.model.recipe.RecipeDetailsDto;
import io.github.joannazadlo.recipedash.repository.entity.Recipe;
import io.github.joannazadlo.recipedash.model.recipe.RecipeSummaryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = IngredientMapper.class)
public interface RecipeMapper {

    RecipeSummaryDto toSummaryDto(Recipe entity);

    RecipeDetailsDto toDetailsDto(Recipe entity);

    Recipe toEntity(RecipeCreateDto dto);
}
