package org.mavb.azure.ai.demos.dto.response;

import lombok.*;

import java.util.List;

/**
 * DTO genérico para respuestas paginadas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponseDto<T> {
    
    /**
     * Lista de elementos de la página actual
     */
    private List<T> data;
    
    /**
     * Información de paginación
     */
    private PaginationDto pagination;
}