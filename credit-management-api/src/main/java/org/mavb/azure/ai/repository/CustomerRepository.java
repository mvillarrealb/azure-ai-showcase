package org.mavb.azure.ai.repository;

import org.mavb.azure.ai.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer entity operations.
 * Used for customer profile management and credit evaluation processes.
 */
@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    /**
     * Find customer by identity document.
     */
    Optional<CustomerEntity> findByIdentityDocumentAndActiveTrue(String identityDocument);

    /**
     * Find customer with their 2 most recent employment records by identity document.
     * Returns customer with employment history limited to 2 most recent jobs.
     */
    @Query("SELECT DISTINCT c FROM CustomerEntity c " +
           "LEFT JOIN FETCH c.employmentHistory eh " +
           "WHERE c.identityDocument = :identityDocument " +
           "AND c.active = true " +
           "AND (eh IS NULL OR eh.id IN (" +
           "  SELECT eh2.id FROM EmploymentHistoryEntity eh2 " +
           "  WHERE eh2.customer.id = c.id " +
           "  ORDER BY eh2.endDate DESC NULLS FIRST, eh2.startDate DESC " +
           "  LIMIT 2" +
           "))")
    Optional<CustomerEntity> findByIdentityDocumentWithRecentEmployments(@Param("identityDocument") String identityDocument);
}