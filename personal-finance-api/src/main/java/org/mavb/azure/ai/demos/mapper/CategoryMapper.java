package org.mavb.azure.ai.demos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mavb.azure.ai.demos.dto.response.CategoryDto;
import org.mavb.azure.ai.demos.model.Category;
import org.mavb.azure.ai.demos.model.CategoryType;

import java.util.List;

/**
 * Mapper para conversiones entre Category entity y CategoryDto
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    /**
     * Convierte una entidad Category a CategoryDto
     */
    @Mapping(target = "type", source = "type", qualifiedByName = "categoryTypeToString")
    CategoryDto toDto(Category category);
    
    /**
     * Convierte una lista de entidades Category a lista de CategoryDto
     */
    List<CategoryDto> toDtoList(List<Category> categories);
    
    /**
     * Convierte CategoryDto a entidad Category
     */
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "type", source = "type", qualifiedByName = "stringToCategoryType")
    Category toEntity(CategoryDto dto);
    
    /**
     * Convierte CategoryType enum a String
     */
    @org.mapstruct.Named("categoryTypeToString")
    default String categoryTypeToString(CategoryType type) {
        return type != null ? type.getValue() : null;
    }
    
    /**
     * Convierte String a CategoryType enum
     */
    @org.mapstruct.Named("stringToCategoryType")
    default CategoryType stringToCategoryType(String type) {
        return type != null ? CategoryType.fromValue(type) : null;
    }
}