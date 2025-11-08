package org.mavb.azure.ai.demos.dto;

import lombok.*;

import java.util.List;

/**
 * DTO para respuestas de listas de reclamos con información de paginación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimListResponseDto {

    private List<ClaimDto> data;
    private PaginationDto pagination;
}