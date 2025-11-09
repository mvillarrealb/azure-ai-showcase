package org.mavb.azure.ai.repository;

import org.mavb.azure.ai.entity.RankEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Rank entity operations.
 * Extends JpaSpecificationExecutor for dynamic query building with Specifications.
 * Follows the standard pattern used across the project (ClaimRepository, TransactionRepository).
 */
@Repository
public interface RankRepository extends JpaRepository<RankEntity, String>, JpaSpecificationExecutor<RankEntity> {

    /**
     * Check if a rank exists by name (case-insensitive).
     * 
     * @param name the rank name to check
     * @return true if exists, false otherwise
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find active ranks with pagination.
     * For complex filtering, use findAll() with RankSpecifications.
     *
     * @param pageable pagination information
     * @return page of active ranks
     */
    Page<RankEntity> findByActiveTrue(Pageable pageable);
}