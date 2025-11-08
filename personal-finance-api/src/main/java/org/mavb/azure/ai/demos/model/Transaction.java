package org.mavb.azure.ai.demos.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad que representa una transacción financiera.
 * Mapeada según el esquema Transaction del OpenAPI.
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transaction_date", columnList = "date"),
    @Index(name = "idx_transaction_category", columnList = "category_id"),
    @Index(name = "idx_transaction_amount", columnList = "amount")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    
    /**
     * Identificador único de la transacción (UUID generado automáticamente)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    
    /**
     * Monto de la transacción (positivo para ingresos, negativo para gastos)
     * Precisión: 10 dígitos totales, 2 decimales
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    /**
     * Fecha y hora de la transacción (formato ISO 8601)
     */
    @Column(nullable = false)
    private LocalDateTime date;
    
    /**
     * Descripción detallada de la transacción
     */
    @Column(nullable = false, length = 500)
    private String description;
    
    /**
     * Categoría asociada a esta transacción
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    /**
     * Fecha de creación del registro (auditoría)
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Fecha de última modificación (auditoría)
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    /**
     * Métodos de conveniencia para determinar tipo de transacción
     */
    public boolean isIncome() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isExpense() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) < 0;
    }
    
    /**
     * Obtiene el monto absoluto (sin signo)
     */
    public BigDecimal getAbsoluteAmount() {
        return amount != null ? amount.abs() : BigDecimal.ZERO;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
               "id=" + id +
               ", amount=" + amount +
               ", date=" + date +
               ", description='" + description + '\'' +
               ", categoryId='" + (category != null ? category.getId() : null) + '\'' +
               '}';
    }
}