package org.mavb.azure.ai.demos.dto;

import lombok.*;

/**
 * DTO para información de paginación en respuestas de listas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationDto {

    private Integer page;
    private Integer limit;
    private Long total;
    private Integer totalPages;
}