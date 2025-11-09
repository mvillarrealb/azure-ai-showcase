package org.mavb.azure.ai.entity;

import jakarta.persistence.*;
import lombok.*;
import org.mavb.azure.ai.listener.RankSyncListener;

import java.time.LocalDateTime;

/**
 * Entity representing a customer rank/classification in the banking system.
 * Maps to the ranks table and contains rank information for customer categorization
 * and AI-powered semantic search capabilities.
 */
@Entity
@Table(name = "ranks")
@EntityListeners(RankSyncListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RankEntity {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

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