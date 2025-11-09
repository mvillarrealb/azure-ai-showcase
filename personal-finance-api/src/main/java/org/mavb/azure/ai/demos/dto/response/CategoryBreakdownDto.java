package org.mavb.azure.ai.demos.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.mavb.azure.ai.demos.model.CategoryType;

import java.math.BigDecimal;

/**
 * DTO para desglose por categoría según esquema CategoryBreakdown del OpenAPI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryBreakdownDto {
    
    /**
     * ID de la categoría
     */
    private String categoryId;
    
    /**
     * Nombre de la categoría
     */
    private String categoryName;
    
    /**
     * Tipo de la categoría (income o expense)
     */
    @JsonProperty("categoryType")
    private String categoryType; // Cambiaremos a String para JSON
    
    /**
     * Monto total para esta categoría (siempre positivo)
     */
    private BigDecimal totalAmount;
    
    /**
     * Constructor helper que convierte CategoryType a String
     */
    public static CategoryBreakdownDto from(String categoryId, String categoryName, CategoryType type, BigDecimal totalAmount) {
        return CategoryBreakdownDto.builder()
            .categoryId(categoryId)
            .categoryName(categoryName)
            .categoryType(type.getValue()) // Convertir enum a string
            .totalAmount(totalAmount)
            .build();
    }
}