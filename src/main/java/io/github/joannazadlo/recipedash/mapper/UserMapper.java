package io.github.joannazadlo.recipedash.mapper;

import io.github.joannazadlo.recipedash.model.user.UserDto;
import io.github.joannazadlo.recipedash.repository.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User entity);

    User toEntity(UserDto dto);
}
