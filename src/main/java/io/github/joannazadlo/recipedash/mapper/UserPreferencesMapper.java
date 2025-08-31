package io.github.joannazadlo.recipedash.mapper;

import io.github.joannazadlo.recipedash.model.userPreferences.UserPreferencesDto;
import io.github.joannazadlo.recipedash.repository.entity.UserPreferences;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPreferencesMapper {

    UserPreferencesDto toUserPreferencesDto(UserPreferences userPreferences);

    UserPreferences toEntity(UserPreferencesDto dto);
}
