package org.mavb.azure.ai.demos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * DTO para filtros de reporte mensual (query parameters)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyReportFilterDto {
    
    /**
     * Mes para el reporte en formato YYYY-MM (ejemplo: 2024-11)
     */
    @NotBlank(message = "El mes es requerido")
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}$", 
             message = "El formato del mes debe ser YYYY-MM (ejemplo: 2024-11)")
    private String month;
}