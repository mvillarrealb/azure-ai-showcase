package org.mavb.azure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mavb.azure.dto.ProductDTO;
import org.mavb.azure.entity.CreditProductEntity;

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
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "subcategory", source = "subcategory")
    @Mapping(target = "minimumAmount", source = "minimumAmount")
    @Mapping(target = "maximumAmount", source = "maximumAmount")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "term", source = "term")
    @Mapping(target = "minimumRate", source = "minimumRate")
    @Mapping(target = "maximumRate", source = "maximumRate")
    @Mapping(target = "requirements", source = "requirements")
    @Mapping(target = "features", source = "features")
    @Mapping(target = "benefits", source = "benefits")
    ProductDTO toDto(CreditProductEntity entity);

    /**
     * Convert ProductDTO to CreditProductEntity.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "subcategory", source = "subcategory")
    @Mapping(target = "minimumAmount", source = "minimumAmount")
    @Mapping(target = "maximumAmount", source = "maximumAmount")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "term", source = "term")
    @Mapping(target = "minimumRate", source = "minimumRate")
    @Mapping(target = "maximumRate", source = "maximumRate")
    @Mapping(target = "requirements", source = "requirements")
    @Mapping(target = "features", source = "features")
    @Mapping(target = "benefits", source = "benefits")
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
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