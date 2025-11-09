package org.mavb.azure.ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing employment history for customers.
 * Used for credit evaluation and employment stability analysis.
 * Tracks employment periods, positions, and income history.
 */
@Entity
@Table(name = "employment_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EmploymentHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer is required")
    private CustomerEntity customer;

    @Column(name = "company_name", nullable = false, length = 255)
    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    private String companyName;

    @Column(name = "position", nullable = false, length = 255)
    @NotBlank(message = "Position is required")
    @Size(max = 255, message = "Position must not exceed 255 characters")
    private String position;

    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "income", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Income is required")
    @DecimalMin(value = "0.01", message = "Income must be positive")
    @Digits(integer = 13, fraction = 2, message = "Income format is invalid")
    private BigDecimal income;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Validates that end date is after start date if provided.
     */
    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateValid() {
        return endDate == null || endDate.isAfter(startDate) || endDate.isEqual(startDate);
    }

    /**
     * Checks if this is a current employment (no end date).
     */
    public boolean isCurrentEmployment() {
        return endDate == null;
    }

    /**
     * Gets the duration of employment in months.
     * For current employment, calculates up to current date.
     */
    public long getEmploymentDurationInMonths() {
        LocalDate endDateToUse = endDate != null ? endDate : LocalDate.now();
        return java.time.temporal.ChronoUnit.MONTHS.between(startDate, endDateToUse);
    }

    /**
     * Gets employment period as string representation.
     */
    public String getEmploymentPeriod() {
        if (endDate == null) {
            return startDate + " a Presente";
        }
        return startDate + " a " + endDate;
    }
}