package org.mavb.azure.ai.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.mavb.azure.ai.listener.ProductSyncListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a credit product in the banking system.
 * Maps to the credit_products table and contains all product information
 * including eligibility criteria, rates, and requirements.
 */
@Entity
@Table(name = "credit_products")
@EntityListeners(ProductSyncListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CreditProductEntity {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "subcategory", nullable = false, length = 100)
    private String subcategory;

    @Column(name = "minimum_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal minimumAmount;

    @Column(name = "maximum_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal maximumAmount;

    @Column(name = "currency", nullable = false, length = 5)
    private String currency;

    @Column(name = "term", nullable = false, length = 50)
    private String term;

    @Column(name = "minimum_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal minimumRate;

    @Column(name = "maximum_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal maximumRate;

    @Type(JsonType.class)
    @Column(name = "requirements", columnDefinition = "jsonb")
    private List<String> requirements;

    @Type(JsonType.class)
    @Column(name = "features", columnDefinition = "jsonb")
    private List<String> features;

    @Type(JsonType.class)
    @Column(name = "benefits", columnDefinition = "jsonb")
    private List<String> benefits;

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
}