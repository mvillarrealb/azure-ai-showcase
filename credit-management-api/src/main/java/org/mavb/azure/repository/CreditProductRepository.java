package org.mavb.azure.repository;

import org.mavb.azure.entity.CreditProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CreditProduct entity operations.
 * Provides custom queries for product filtering and search functionality.
 */
@Repository
public interface CreditProductRepository extends JpaRepository<CreditProductEntity, String> {

    /**
     * Find all active products.
     */
    Page<CreditProductEntity> findByActiveTrue(Pageable pageable);

    /**
     * Find active product by ID.
     */
    Optional<CreditProductEntity> findByIdAndActiveTrue(String id);

    /**
     * Find products by category with pagination.
     */
    Page<CreditProductEntity> findByCategoryAndActiveTrue(String category, Pageable pageable);

    /**
     * Find products by currency with pagination.
     */
    Page<CreditProductEntity> findByCurrencyAndActiveTrue(String currency, Pageable pageable);

    /**
     * Custom query to find products with complex filtering.
     * Supports filtering by category, currency, and amount range.
     */
    @Query("SELECT p FROM CreditProductEntity p WHERE p.active = true " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:currency IS NULL OR p.currency = :currency) " +
           "AND (:minAmount IS NULL OR p.maximumAmount >= :minAmount) " +
           "AND (:maxAmount IS NULL OR p.minimumAmount <= :maxAmount)")
    Page<CreditProductEntity> findWithFilters(
            @Param("category") String category,
            @Param("currency") String currency,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            Pageable pageable
    );

    /**
     * Find products that match amount range and currency for evaluation.
     */
    @Query("SELECT p FROM CreditProductEntity p WHERE p.active = true " +
           "AND p.currency = :currency " +
           "AND p.minimumAmount <= :amount " +
           "AND p.maximumAmount >= :amount " +
           "AND (:category IS NULL OR p.category = :category)")
    List<CreditProductEntity> findEligibleProducts(
            @Param("amount") BigDecimal amount,
            @Param("currency") String currency,
            @Param("category") String category
    );

    /**
     * Find all distinct categories.
     */
    @Query("SELECT DISTINCT p.category FROM CreditProductEntity p WHERE p.active = true")
    List<String> findDistinctCategories();

    /**
     * Find all distinct currencies.
     */
    @Query("SELECT DISTINCT p.currency FROM CreditProductEntity p WHERE p.active = true")
    List<String> findDistinctCurrencies();

    /**
     * Count products by category.
     */
    long countByCategoryAndActiveTrue(String category);
}