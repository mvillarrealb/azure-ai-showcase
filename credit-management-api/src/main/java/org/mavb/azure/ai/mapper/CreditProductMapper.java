package org.mavb.azure.ai.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mavb.azure.ai.dto.response.ProductDTO;
import org.mavb.azure.ai.entity.CreditProductEntity;

import java.util.List;

/**
 * MapStruct mapper for converting between CreditProductEntity and ProductDTO.
 * Handles automatic mapping of product data for API responses.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreditProductMapper {

    /**
     * Convert CreditProductEntity to ProductDTO.
     */
    ProductDTO toDto(CreditProductEntity entity);

    /**
     * Convert ProductDTO to CreditProductEntity.
     */
    CreditProductEntity toEntity(ProductDTO dto);

    /**
     * Convert list of CreditProductEntity to list of ProductDTO.
     */
    List<ProductDTO> toDtoList(List<CreditProductEntity> entities);

    /**
     * Convert list of ProductDTO to list of CreditProductEntity.
     */
    List<CreditProductEntity> toEntityList(List<ProductDTO> dtos);
}