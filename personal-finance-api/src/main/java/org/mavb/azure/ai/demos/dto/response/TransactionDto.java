package org.mavb.azure.ai.demos.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta para transacciones según esquema Transaction del OpenAPI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    
    /**
     * Identificador único de la transacción
     */
    private UUID id;
    
    /**
     * Monto de la transacción (positivo para ingresos, negativo para gastos)
     */
    private BigDecimal amount;
    
    /**
     * Fecha y hora de la transacción en formato ISO 8601
     */
    private LocalDateTime date;
    
    /**
     * ID de la categoría asociada a la transacción
     */
    @JsonProperty("categoryId")
    private String categoryId;
    
    /**
     * Nombre de la categoría asociada a la transacción
     */
    @JsonProperty("categoryName")
    private String categoryName;
    
    /**
     * Tipo de la categoría asociada a la transacción
     */
    @JsonProperty("categoryType")
    private String categoryType;
    
    /**
     * Descripción detallada de la transacción
     */
    private String description;
}