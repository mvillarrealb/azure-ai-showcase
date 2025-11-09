package org.mavb.azure.ai.repository;

import org.mavb.azure.ai.entity.CreditProductEntity;
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

    Optional<CreditProductEntity> findByIdAndActiveTrue(String id);


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

}