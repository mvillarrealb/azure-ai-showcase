package org.mavb.azure.ai.demos.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.demos.dto.request.MonthlyReportFilterDto;
import org.mavb.azure.ai.demos.dto.response.CategoryBreakdownDto;
import org.mavb.azure.ai.demos.dto.response.MonthlyReportDto;
import org.mavb.azure.ai.demos.exception.NotFoundException;
import org.mavb.azure.ai.demos.model.CategoryType;
import org.mavb.azure.ai.demos.repository.TransactionRepository;
import org.mavb.azure.ai.demos.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de reportes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {
    
    private final TransactionRepository transactionRepository;
    
    @Override
    @Transactional(readOnly = true)
    public MonthlyReportDto generateMonthlyReport(MonthlyReportFilterDto filter) {
        log.debug("Generando reporte mensual para: {}", filter.getMonth());
        
        // Parsear año y mes
        YearMonth yearMonth = parseYearMonth(filter.getMonth());
        int year = yearMonth.getYear();
        int month = yearMonth.getMonthValue();
        
        log.debug("Procesando reporte para año: {}, mes: {}", year, month);
        
        // Calcular totales por tipo de categoría
        BigDecimal totalIncome = transactionRepository.getTotalByCategoryTypeAndMonth(
            CategoryType.INCOME, year, month);
        BigDecimal totalExpense = transactionRepository.getTotalByCategoryTypeAndMonth(
            CategoryType.EXPENSE, year, month);
        
        // Los gastos se almacenan como negativos, convertir a positivo para el reporte
        BigDecimal totalExpensePositive = totalExpense.abs();
        
        // Calcular ahorro neto
        BigDecimal netSavings = totalIncome.subtract(totalExpensePositive);
        
        // Obtener desglose por categoría
        List<CategoryBreakdownDto> categoryBreakdown = getCategoryBreakdown(year, month);
        
        log.debug("Reporte generado - Ingresos: {}, Gastos: {}, Ahorro: {}", 
                 totalIncome, totalExpensePositive, netSavings);
        
        // Verificar si hay datos para el mes
        if (totalIncome.compareTo(BigDecimal.ZERO) == 0 && 
            totalExpensePositive.compareTo(BigDecimal.ZERO) == 0 && 
            categoryBreakdown.isEmpty()) {
            throw new NotFoundException("No se encontraron datos para el mes especificado: " + filter.getMonth());
        }
        
        return MonthlyReportDto.builder()
            .month(filter.getMonth())
            .totalIncome(totalIncome)
            .totalExpense(totalExpensePositive)
            .netSavings(netSavings)
            .categoryBreakdown(categoryBreakdown)
            .build();
    }
    
    /**
     * Parsea el string de mes en formato YYYY-MM a YearMonth
     */
    private YearMonth parseYearMonth(String monthString) {
        try {
            return YearMonth.parse(monthString);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de mes inválido. Debe ser YYYY-MM (ejemplo: 2024-11)");
        }
    }
    
    /**
     * Obtiene el desglose de montos por categoría para un mes específico
     */
    private List<CategoryBreakdownDto> getCategoryBreakdown(int year, int month) {
        List<Object[]> rawData = transactionRepository.getCategoryBreakdownByMonth(year, month);
        
        return rawData.stream()
            .map(row -> CategoryBreakdownDto.builder()
                .categoryId((String) row[0])
                .totalAmount((BigDecimal) row[1])
                .build())
            .collect(Collectors.toList());
    }
}