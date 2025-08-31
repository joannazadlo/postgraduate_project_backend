package io.github.joannazadlo.recipedash.mapper;

import io.github.joannazadlo.recipedash.model.rating.SaveOpinionDto;
import io.github.joannazadlo.recipedash.repository.entity.Opinion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OpinionMapper {

    SaveOpinionDto toDto(Opinion opinion);

    Opinion toEntity(SaveOpinionDto dto);
}
