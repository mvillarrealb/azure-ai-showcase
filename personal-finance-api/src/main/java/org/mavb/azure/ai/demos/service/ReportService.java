package org.mavb.azure.ai.demos.service;

import org.mavb.azure.ai.demos.dto.request.MonthlyReportFilterDto;
import org.mavb.azure.ai.demos.dto.response.MonthlyReportDto;

/**
 * Interfaz del servicio para operaciones de reportes
 */
public interface ReportService {
    
    /**
     * Genera un reporte financiero mensual
     * @param filter Filtro con el mes a consultar
     * @return Reporte mensual con totales y desglose por categoría
     */
    MonthlyReportDto generateMonthlyReport(MonthlyReportFilterDto filter);
}