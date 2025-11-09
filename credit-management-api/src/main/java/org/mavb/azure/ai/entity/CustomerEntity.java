package org.mavb.azure.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;

/**
 * Entity representing a customer in the banking system.
 * Used internally for credit evaluation and customer profile management.
 * Not directly exposed through API endpoints but essential for credit evaluation logic.
 */
@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "identity_document", nullable = false, unique = true, length = 20)
    private String identityDocument;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "monthly_income", precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "current_debt", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal currentDebt = BigDecimal.ZERO;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EmploymentHistoryEntity> employmentHistory = new ArrayList<>();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calculates the total months of unemployment until the current employment.
     * Analyzes gaps between employment periods to determine unemployment duration.
     * 
     * @return Total months of unemployment between jobs
     */
    public long calculateUnemploymentMonthsUntilCurrent() {
        if (employmentHistory == null || employmentHistory.isEmpty()) {
            return 0;
        }

        // Sort employment history by start date (ascending)
        List<EmploymentHistoryEntity> sortedHistory = employmentHistory.stream()
                .sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate()))
                .toList();

        long totalUnemploymentMonths = 0;

        // Calculate gaps between consecutive employments
        for (int i = 0; i < sortedHistory.size() - 1; i++) {
            EmploymentHistoryEntity currentJob = sortedHistory.get(i);
            EmploymentHistoryEntity nextJob = sortedHistory.get(i + 1);

            // Skip if current job has no end date (still current)
            if (currentJob.getEndDate() == null) {
                continue;
            }

            // Calculate gap between end of current job and start of next job
            LocalDate gapStart = currentJob.getEndDate().plusDays(1);
            LocalDate gapEnd = nextJob.getStartDate().minusDays(1);

            if (gapEnd.isAfter(gapStart) || gapEnd.isEqual(gapStart)) {
                long gapMonths = ChronoUnit.MONTHS.between(gapStart, gapEnd.plusDays(1));
                totalUnemploymentMonths += gapMonths;
            }
        }

        return totalUnemploymentMonths;
    }

    /**
     * Gets the most recent employment records.
     * 
     * @param limit Maximum number of records to return
     * @return List of most recent employment records
     */
    public List<EmploymentHistoryEntity> getMostRecentEmployments(int limit) {
        if (employmentHistory == null || employmentHistory.isEmpty()) {
            return new ArrayList<>();
        }

        return employmentHistory.stream()
                .sorted((e1, e2) -> {
                    // Current jobs (no end date) come first
                    if (e1.getEndDate() == null && e2.getEndDate() != null) return -1;
                    if (e1.getEndDate() != null && e2.getEndDate() == null) return 1;
                    
                    // For jobs with end dates, sort by end date descending
                    if (e1.getEndDate() != null && e2.getEndDate() != null) {
                        return e2.getEndDate().compareTo(e1.getEndDate());
                    }
                    
                    // For current jobs, sort by start date descending
                    return e2.getStartDate().compareTo(e1.getStartDate());
                })
                .limit(limit)
                .toList();
    }

    /**
     * Gets the current employment (job with no end date).
     * 
     * @return Current employment or null if none exists
     */
    public EmploymentHistoryEntity getCurrentEmployment() {
        if (employmentHistory == null || employmentHistory.isEmpty()) {
            return null;
        }

        return employmentHistory.stream()
                .filter(EmploymentHistoryEntity::isCurrentEmployment)
                .findFirst()
                .orElse(null);
    }
}