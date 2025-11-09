package org.mavb.azure.ai.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents an employment record for customer employment history analysis.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentRecord {
    
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal income;
}