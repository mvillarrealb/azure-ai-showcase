package org.mavb.azure.ai.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mavb.azure.ai.dto.response.RankDTO;
import org.mavb.azure.ai.entity.RankEntity;

import java.util.List;

/**
 * MapStruct mapper for converting between RankEntity and RankDTO.
 * Handles automatic mapping of rank data for API responses.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RankMapper {

    /**
     * Convert RankEntity to RankDTO.
     */
    RankDTO toDto(RankEntity entity);

    /**
     * Convert RankDTO to RankEntity.
     * Ignores audit fields that are not present in DTO.
     */
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RankEntity toEntity(RankDTO dto);

    /**
     * Convert list of RankEntity to list of RankDTO.
     */
    List<RankDTO> toDtoList(List<RankEntity> entities);

    /**
     * Convert list of RankDTO to list of RankEntity.
     * Ignores audit fields that are not present in DTOs.
     */
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    List<RankEntity> toEntityList(List<RankDTO> dtos);
}