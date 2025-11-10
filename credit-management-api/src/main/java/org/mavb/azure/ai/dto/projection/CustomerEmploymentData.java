package org.mavb.azure.ai.dto.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Interface-based JPA projection for customer employment data from native query.
 * Spring JPA will automatically map the query result columns to this interface.
 */
public interface CustomerEmploymentData {
    
    String getIdentityDocument();
    BigDecimal getMonthlyIncome();
    BigDecimal getCurrentDebt();
    LocalDate getStartDate();
    LocalDate getEndDate();
    BigDecimal getIncome();
}