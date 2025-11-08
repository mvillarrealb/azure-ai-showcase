package org.mavb.azure.ai.demos.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO de respuesta para categorías según esquema Category del OpenAPI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    
    /**
     * Identificador único de la categoría
     */
    private String id;
    
    /**
     * Nombre descriptivo de la categoría
     */
    private String name;
    
    /**
     * Tipo de categoría: "income" o "expense"
     */
    private String type;
}