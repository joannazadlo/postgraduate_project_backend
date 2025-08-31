package io.github.joannazadlo.recipedash.mapper;

import io.github.joannazadlo.recipedash.model.recipeIngredient.RecipeIngredientCreateDto;
import io.github.joannazadlo.recipedash.model.recipeIngredient.RecipeIngredientDto;
import io.github.joannazadlo.recipedash.model.recipeIngredient.RecipeIngredientUpdateDto;
import io.github.joannazadlo.recipedash.repository.entity.RecipeIngredient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredientMapper {

    RecipeIngredientDto toDto(RecipeIngredient entity);

    RecipeIngredient toCreateEntity(RecipeIngredientCreateDto dto);

    RecipeIngredient toUpdateEntity(RecipeIngredientUpdateDto dto);
}
