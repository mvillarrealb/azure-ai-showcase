package org.mavb.azure.ai.demos.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa una categoría de transacción financiera.
 * Mapeada según el esquema Category del OpenAPI.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    
    /**
     * Identificador único de la categoría (cat-001, cat-002, etc.)
     */
    @Id
    @Column(length = 50, nullable = false)
    private String id;
    
    /**
     * Nombre descriptivo de la categoría (ej: "Alimentación")
     */
    @Column(nullable = false, length = 100)
    private String name;
    
    /**
     * Tipo de categoría: INCOME o EXPENSE
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoryType type;
    
    /**
     * Transacciones asociadas a esta categoría
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
    
    // Métodos helper para mantener sincronización bidireccional
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        transaction.setCategory(this);
    }
    
    public void removeTransaction(Transaction transaction) {
        this.transactions.remove(transaction);
        transaction.setCategory(null);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Category{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", type=" + type +
               '}';
    }
}