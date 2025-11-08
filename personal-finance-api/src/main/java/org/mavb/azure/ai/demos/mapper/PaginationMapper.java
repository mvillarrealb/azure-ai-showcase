package org.mavb.azure.ai.demos.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.mavb.azure.ai.demos.dto.response.PaginatedResponseDto;
import org.mavb.azure.ai.demos.dto.response.PaginationDto;

/**
 * Mapper para conversiones de paginación
 */
@Mapper(componentModel = "spring")
public interface PaginationMapper {
    
    /**
     * Convierte información de Page a PaginationDto
     */
    default PaginationDto toPaginationDto(Page<?> page) {
        if (page == null) {
            return null;
        }
        
        return PaginationDto.builder()
            .currentPage(page.getNumber() + 1) // Page es 0-indexed, DTO es 1-indexed
            .totalPages(page.getTotalPages())
            .totalItems(page.getTotalElements())
            .itemsPerPage(page.getSize())
            .build();
    }
    
    /**
     * Crea una respuesta paginada genérica
     */
    default <T> PaginatedResponseDto<T> toPaginatedResponse(Page<T> page) {
        if (page == null) {
            return null;
        }
        
        return PaginatedResponseDto.<T>builder()
            .transactions(page.getContent())
            .pagination(toPaginationDto(page))
            .build();
    }
}