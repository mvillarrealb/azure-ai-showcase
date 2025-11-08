package org.mavb.azure.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "credit_score")
    private Integer creditScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", length = 20)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20)
    private RiskLevel riskLevel;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Enum representing employment types for risk assessment
     */
    public enum EmploymentType {
        DEPENDIENTE,    // Employee
        INDEPENDIENTE,  // Freelancer
        EMPRESARIO      // Business Owner
    }

    /**
     * Enum representing risk levels based on credit evaluation
     */
    public enum RiskLevel {
        BAJO,   // Low Risk
        MEDIO,  // Medium Risk
        ALTO    // High Risk
    }
}