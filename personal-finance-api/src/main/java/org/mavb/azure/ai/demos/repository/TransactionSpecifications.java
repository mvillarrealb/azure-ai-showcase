package org.mavb.azure.ai.demos.repository;

import org.mavb.azure.ai.demos.model.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * Especificaciones JPA para construir consultas dinámicas de Transaction
 */
public class TransactionSpecifications {
    
    /**
     * Filtra transacciones por categoría
     */
    public static Specification<Transaction> hasCategory(String categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null || categoryId.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }
    
    /**
     * Filtra transacciones desde una fecha específica (inclusive)
     */
    public static Specification<Transaction> hasDateAfterOrEqual(LocalDateTime startDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate);
        };
    }
    
    /**
     * Filtra transacciones hasta una fecha específica (inclusive)
     */
    public static Specification<Transaction> hasDateBeforeOrEqual(LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            if (endDate == null) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate);
        };
    }
    
    /**
     * Filtra transacciones en un rango de fechas
     */
    public static Specification<Transaction> hasDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return Specification.where(hasDateAfterOrEqual(startDate))
                           .and(hasDateBeforeOrEqual(endDate));
    }
    
    /**
     * Filtra transacciones que contengan un texto en la descripción (case insensitive)
     */
    public static Specification<Transaction> hasDescriptionContaining(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return null;
            }
            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("description")), 
                likePattern
            );
        };
    }
    
    /**
     * Filtra transacciones con monto positivo (ingresos)
     */
    public static Specification<Transaction> isIncome() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.greaterThan(root.get("amount"), 0);
    }
    
    /**
     * Filtra transacciones con monto negativo (gastos)
     */
    public static Specification<Transaction> isExpense() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.lessThan(root.get("amount"), 0);
    }
    
    /**
     * Ordena por fecha descendente (más recientes primero)
     */
    public static Specification<Transaction> orderByDateDesc() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("date")));
            return null;
        };
    }
}