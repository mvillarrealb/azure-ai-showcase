package org.mavb.azure.repository;

import org.mavb.azure.entity.CustomerEntity;
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
     * Check if customer exists by identity document.
     */
    boolean existsByIdentityDocumentAndActiveTrue(String identityDocument);

    /**
     * Find customers by risk level.
     */
    List<CustomerEntity> findByRiskLevelAndActiveTrue(CustomerEntity.RiskLevel riskLevel);

    /**
     * Find customers by employment type.
     */
    List<CustomerEntity> findByEmploymentTypeAndActiveTrue(CustomerEntity.EmploymentType employmentType);

    /**
     * Find customers with credit score in range.
     */
    @Query("SELECT c FROM CustomerEntity c WHERE c.active = true " +
           "AND c.creditScore >= :minScore " +
           "AND c.creditScore <= :maxScore")
    List<CustomerEntity> findByCreditScoreRange(
            @Param("minScore") Integer minScore,
            @Param("maxScore") Integer maxScore
    );

    /**
     * Find customers by email.
     */
    Optional<CustomerEntity> findByEmailAndActiveTrue(String email);
}