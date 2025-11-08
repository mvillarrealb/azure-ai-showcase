package org.mavb.azure.ai.demos.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de respuesta para reporte mensual según esquema MonthlyReport del OpenAPI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyReportDto {
    
    /**
     * Mes del reporte en formato YYYY-MM
     */
    private String month;
    
    /**
     * Total de ingresos del mes
     */
    private BigDecimal totalIncome;
    
    /**
     * Total de gastos del mes (valor positivo)
     */
    private BigDecimal totalExpense;
    
    /**
     * Ahorro neto del mes (ingresos - gastos)
     */
    private BigDecimal netSavings;
    
    /**
     * Desglose detallado por categoría
     */
    private List<CategoryBreakdownDto> categoryBreakdown;
}