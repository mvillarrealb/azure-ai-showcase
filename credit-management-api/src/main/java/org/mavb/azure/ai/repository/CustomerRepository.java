package org.mavb.azure.ai.repository;

import org.mavb.azure.ai.dto.projection.CustomerEmploymentData;
import org.mavb.azure.ai.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer entity operations.
 */
@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    /**
     * Finds customer with recent employment records.
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

    /**
     * Finds customer employment data for semantic analysis.
     */
    @Query(value = """
        SELECT c.identity_document as identityDocument,
               c.monthly_income as monthlyIncome, 
               c.current_debt as currentDebt, 
               eh.start_date as startDate,
               eh.end_date as endDate,
               eh.income as income
        FROM customers c
        LEFT JOIN LATERAL (
            SELECT start_date, end_date, income
            FROM employment_history
            WHERE customer_id = c.id
            ORDER BY end_date DESC NULLS FIRST, start_date DESC
            LIMIT 2
        ) eh ON TRUE
        WHERE c.identity_document = :identityDocument 
          AND c.active = true
        """, nativeQuery = true)
    List<CustomerEmploymentData> findCustomerEmploymentDataForSemanticAnalysis(@Param("identityDocument") String identityDocument);
}