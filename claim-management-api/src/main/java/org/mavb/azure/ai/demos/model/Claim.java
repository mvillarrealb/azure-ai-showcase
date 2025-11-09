package org.mavb.azure.ai.demos.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un reclamo de cliente en el sistema.
 * Esta entidad se mapea a la tabla 'claims' en PostgreSQL.
 */
@Entity
@Table(name = "claims")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Claim {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "identity_document", nullable = false, length = 12)
    private String identityDocument;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "reason", nullable = false, length = 100)
    private String reason;

    @Column(name = "sub_reason", nullable = false, length = 100)
    private String subReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ClaimStatus status;

    @Column(name = "comments", length = 1000)
    private String comments;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    /**
     * Enum que define los posibles estados de un reclamo
     */
    public enum ClaimStatus {
        open, inProgress, resolved
    }

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = ClaimStatus.open;
        }
        if (this.id == null || this.id.isEmpty()) {
            this.id = generateClaimId();
        }
    }

    /**
     * Genera un ID único para el reclamo siguiendo el patrón CLM-YYYY-NNNNNN
     */
    private String generateClaimId() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String randomSuffix = String.format("%06d", (int) (Math.random() * 1000000));
        return "CLM-" + year + "-" + randomSuffix;
    }
}