package org.mavb.azure.ai.demos.repository;

import org.mavb.azure.ai.demos.model.CategoryType;
import org.mavb.azure.ai.demos.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio para operaciones CRUD de Transaction con soporte para especificaciones
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {
    
    /**
     * Busca transacciones por categoría con paginación
     */
    Page<Transaction> findByCategoryId(String categoryId, Pageable pageable);
    
    /**
     * Busca transacciones en un rango de fechas
     */
    @Query("SELECT t FROM Transaction t WHERE t.date >= :startDate AND t.date <= :endDate ORDER BY t.date DESC")
    List<Transaction> findByDateBetweenOrderByDateDesc(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Busca transacciones por categoría y rango de fechas
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.category c " +
           "WHERE c.id = :categoryId " +
           "AND t.date >= :startDate " +
           "AND t.date <= :endDate " +
           "ORDER BY t.date DESC")
    List<Transaction> findByCategoryAndDateRange(
        @Param("categoryId") String categoryId,
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Calcula el total de transacciones por tipo de categoría en un mes específico
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) " +
           "FROM Transaction t " +
           "JOIN t.category c " +
           "WHERE c.type = :categoryType " +
           "AND YEAR(t.date) = :year " +
           "AND MONTH(t.date) = :month")
    BigDecimal getTotalByCategoryTypeAndMonth(
        @Param("categoryType") CategoryType categoryType,
        @Param("year") int year,
        @Param("month") int month
    );
    
    /**
     * Obtiene el desglose de montos por categoría para un mes específico
     */
    @Query("SELECT c.id, c.name, c.type, COALESCE(SUM(t.amount), 0) " +
           "FROM Transaction t " +
           "JOIN t.category c " +
           "WHERE YEAR(t.date) = :year " +
           "AND MONTH(t.date) = :month " +
           "GROUP BY c.id, c.name, c.type " +
           "ORDER BY c.name")
    List<Object[]> getCategoryBreakdownByMonth(
        @Param("year") int year,
        @Param("month") int month
    );
    
    /**
     * Busca transacciones por descripción (búsqueda de texto)
     */
    @Query("SELECT t FROM Transaction t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY t.date DESC")
    Page<Transaction> findByDescriptionContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Obtiene las últimas N transacciones
     */
    @Query("SELECT t FROM Transaction t ORDER BY t.date DESC")
    List<Transaction> findTopNByOrderByDateDesc(Pageable pageable);
    
    /**
     * Calcula el total de ingresos y gastos por categoría
     */
    @Query("SELECT c.id, c.name, c.type, " +
           "COUNT(t), MIN(t.amount), MAX(t.amount), AVG(t.amount), SUM(t.amount) " +
           "FROM Transaction t " +
           "JOIN t.category c " +
           "GROUP BY c.id, c.name, c.type " +
           "ORDER BY c.type, SUM(t.amount) DESC")
    List<Object[]> getTransactionStatsByCategory();
    
    /**
     * Verifica si existen transacciones para una categoría
     */
    boolean existsByCategoryId(String categoryId);
    
    /**
     * Cuenta transacciones en un rango de fechas
     */
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.date >= :startDate AND t.date <= :endDate")
    long countTransactionsInDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}