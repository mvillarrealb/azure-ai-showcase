package org.mavb.azure.ai.repository;

import org.mavb.azure.ai.entity.RankEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Rank entity operations.
 * Provides custom queries for rank filtering and search functionality.
 */
@Repository
public interface RankRepository extends JpaRepository<RankEntity, String> {

    /**
     * Custom query to find ranks with filtering capabilities.
     * Supports filtering by name (partial match) and active status.
     */
    @Query("SELECT r FROM RankEntity r WHERE r.active = true " +
           "AND (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<RankEntity> findWithFilters(
            @Param("name") String name,
            Pageable pageable
    );
}