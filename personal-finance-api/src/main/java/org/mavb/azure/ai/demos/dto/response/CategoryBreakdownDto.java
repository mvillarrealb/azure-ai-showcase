package org.mavb.azure.ai.demos.dto.response;

import lombok.*;

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
     * Monto total para esta categoría (positivo para ingresos, negativo para gastos)
     */
    private BigDecimal totalAmount;
}