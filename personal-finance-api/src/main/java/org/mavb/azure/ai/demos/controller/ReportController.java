package org.mavb.azure.ai.demos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.demos.dto.request.MonthlyReportFilterDto;
import org.mavb.azure.ai.demos.dto.response.MonthlyReportDto;
import org.mavb.azure.ai.demos.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para operaciones de reportes según tag "Reportes" del OpenAPI
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ReportController {
    
    private final ReportService reportService;
    
    /**
     * Obtener reporte financiero mensual
     * Endpoint: GET /reports/monthly
     * 
     * Devuelve un informe detallado de gastos e ingresos para un mes específico, 
     * incluyendo totales y desglose por categorías.
     */
    @GetMapping("/reports/monthly")
    public ResponseEntity<MonthlyReportDto> getMonthlyReport(
            @Valid @ModelAttribute MonthlyReportFilterDto filter) {
        
        log.debug("GET /api/v1/reports/monthly - Generando reporte para mes: {}", filter.getMonth());
        
        MonthlyReportDto report = reportService.generateMonthlyReport(filter);
        
        log.debug("Reporte generado exitosamente para mes: {} - Ingresos: {}, Gastos: {}, Ahorro: {}", 
                 filter.getMonth(), 
                 report.getTotalIncome(), 
                 report.getTotalExpense(), 
                 report.getNetSavings());
        
        return ResponseEntity.ok(report);
    }
}