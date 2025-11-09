package org.mavb.azure.ai.dto.projection;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA class-based projection for customer employment data with semantic description generation.
 * Handles employment gap calculation and generates semantic profiles for AI Search.
 */
@Data
@NoArgsConstructor
public class CustomerEmploymentProjection {
    
    private String identityDocument;
    private BigDecimal monthlyIncome;
    private BigDecimal currentDebt;
    private List<EmploymentRecord> employmentHistory = new ArrayList<>();
    
    public CustomerEmploymentProjection(String identityDocument, BigDecimal monthlyIncome, BigDecimal currentDebt) {
        this.identityDocument = identityDocument;
        this.monthlyIncome = monthlyIncome;
        this.currentDebt = currentDebt;
        this.employmentHistory = new ArrayList<>();
    }
    
    /**
     * Adds employment record to the projection.
     *
     * @param startDate Employment start date
     * @param endDate Employment end date (null for current employment)
     * @param income Employment income
     */
    public void addEmploymentRecord(LocalDate startDate, LocalDate endDate, BigDecimal income) {
        employmentHistory.add(new EmploymentRecord(startDate, endDate, income));
    }
    
    /**
     * Calculates employment gap between the two most recent employments.
     *
     * @return Employment gap analysis with duration and description
     */
    public EmploymentGapAnalysis calculateEmploymentGap() {
        if (employmentHistory.isEmpty()) {
            return new EmploymentGapAnalysis(0, "no ha tenido empleos previos");
        }
        
        if (employmentHistory.size() == 1) {
            return new EmploymentGapAnalysis(0, "solo tiene empleo actual");
        }
        
        EmploymentRecord current = employmentHistory.get(0);
        EmploymentRecord previous = employmentHistory.get(1);
        
        if (previous.getEndDate() == null) {
            return new EmploymentGapAnalysis(0, "historial de empleo inconsistente");
        }
        
        long gapDays = ChronoUnit.DAYS.between(previous.getEndDate(), current.getStartDate());
        
        if (gapDays <= 30) {
            return new EmploymentGapAnalysis(0, "no ha tenido interrupciones laborales entre su ultimo empleo y el actual");
        } else {
            long gapMonths = ChronoUnit.MONTHS.between(previous.getEndDate(), current.getStartDate());
            String description = String.format("tuvo %d meses de interrupción laboral entre empleos", gapMonths);
            return new EmploymentGapAnalysis(gapMonths, description);
        }
    }
    
    /**
     * Generates semantic description for AI Search rank resolution.
     *
     * @return Semantic description string for customer profile
     */
    public String generateSemanticDescription() {
        EmploymentGapAnalysis gapAnalysis = calculateEmploymentGap();
        
        StringBuilder description = new StringBuilder();
        
        description.append("Cliente con ingresos de ")
                  .append(monthlyIncome != null ? monthlyIncome : BigDecimal.ZERO)
                  .append(" soles y deuda actual de ")
                  .append(currentDebt != null ? currentDebt : BigDecimal.ZERO)
                  .append(" soles. ");
        
        description.append(gapAnalysis.getDescription()).append(".");
        
        return description.toString();
    }
}