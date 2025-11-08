package org.mavb.azure.ai.demos.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para crear transacciones según esquema TransactionCreate del OpenAPI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransactionDto {
    
    /**
     * Monto de la transacción (positivo para ingresos, negativo para gastos)
     */
    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "-999999.99", message = "El monto está fuera del rango permitido")
    @DecimalMax(value = "999999.99", message = "El monto está fuera del rango permitido")
    @Digits(integer = 8, fraction = 2, message = "El monto debe tener máximo 8 dígitos enteros y 2 decimales")
    private BigDecimal amount;
    
    /**
     * Fecha y hora de la transacción en formato ISO 8601
     */
    @NotNull(message = "La fecha es requerida")
    private LocalDateTime date;
    
    /**
     * ID de la categoría asociada a la transacción
     */
    @NotBlank(message = "El ID de categoría es requerido")
    @Size(max = 50, message = "El ID de categoría no puede exceder 50 caracteres")
    @JsonProperty("categoryId")
    private String categoryId;
    
    /**
     * Descripción detallada de la transacción
     */
    @NotBlank(message = "La descripción es requerida")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
}