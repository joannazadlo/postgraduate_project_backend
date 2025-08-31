package io.github.joannazadlo.recipedash.mapper;

import io.github.joannazadlo.recipedash.model.userIngredient.UserIngredientCreateDto;
import io.github.joannazadlo.recipedash.model.userIngredient.UserIngredientDto;
import io.github.joannazadlo.recipedash.repository.entity.UserIngredient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserIngredientMapper {

    UserIngredientDto toDto(UserIngredient userIngredient);

    UserIngredient toEntity(UserIngredientCreateDto dto);
}
