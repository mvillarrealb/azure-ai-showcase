package org.mavb.azure.ai.demos.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO para información de paginación según esquema Pagination del OpenAPI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationDto {
    
    /**
     * Página actual
     */
    @JsonProperty("currentPage")
    private Integer currentPage;
    
    /**
     * Total de páginas disponibles
     */
    @JsonProperty("totalPages")
    private Integer totalPages;
    
    /**
     * Total de elementos en toda la colección
     */
    @JsonProperty("totalItems")
    private Long totalItems;
    
    /**
     * Número de elementos por página
     */
    @JsonProperty("itemsPerPage")
    private Integer itemsPerPage;
}