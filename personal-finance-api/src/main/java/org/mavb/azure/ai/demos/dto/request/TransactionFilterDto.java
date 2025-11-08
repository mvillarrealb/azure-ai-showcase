package org.mavb.azure.ai.demos.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para filtros de búsqueda de transacciones (query parameters)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionFilterDto {
    
    /**
     * Número de página para la paginación (comienza en 1)
     */
    @Min(value = 1, message = "La página debe ser mayor o igual a 1")
    @Builder.Default
    private Integer page = 1;
    
    /**
     * Número máximo de transacciones por página
     */
    @Min(value = 1, message = "El límite debe ser al menos 1")
    @Max(value = 100, message = "El límite máximo es 100")
    @Builder.Default
    private Integer limit = 20;
    
    /**
     * Filtrar transacciones por ID de categoría específica
     */
    @Size(max = 50, message = "El ID de categoría no puede exceder 50 caracteres")
    @JsonProperty("categoryId")
    private String categoryId;
    
    /**
     * Fecha de inicio para filtrar transacciones (formato ISO 8601)
     */
    @JsonProperty("startDate")
    private LocalDateTime startDate;
    
    /**
     * Fecha de fin para filtrar transacciones (formato ISO 8601)
     */
    @JsonProperty("endDate")
    private LocalDateTime endDate;
    
    /**
     * Validación cruzada: startDate debe ser anterior a endDate
     */
    @AssertTrue(message = "La fecha de inicio debe ser anterior a la fecha de fin")
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) {
            return true; // Si alguna es null, no validamos
        }
        return !startDate.isAfter(endDate);
    }
}