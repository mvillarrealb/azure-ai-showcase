package org.mavb.azure.ai.demos.repository;

import org.mavb.azure.ai.demos.model.Claim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para la entidad Claim.
 * Proporciona operaciones CRUD y consultas personalizadas.
 */
@Repository
public interface ClaimRepository extends JpaRepository<Claim, String>, JpaSpecificationExecutor<Claim> {

    /**
     * Busca reclamos por documento de identidad con paginación.
     *
     * @param identityDocument documento de identidad del cliente
     * @param pageable información de paginación
     * @return página de reclamos
     */
    Page<Claim> findByIdentityDocument(String identityDocument, Pageable pageable);

    /**
     * Busca reclamos por estado con paginación.
     *
     * @param status estado del reclamo
     * @param pageable información de paginación
     * @return página de reclamos
     */
    Page<Claim> findByStatus(Claim.ClaimStatus status, Pageable pageable);

    /**
     * Busca reclamos por documento de identidad y estado con paginación.
     *
     * @param identityDocument documento de identidad del cliente
     * @param status estado del reclamo
     * @param pageable información de paginación
     * @return página de reclamos
     */
    Page<Claim> findByIdentityDocumentAndStatus(String identityDocument, Claim.ClaimStatus status, Pageable pageable);

    /**
     * Verifica si existe un reclamo con un ID específico.
     *
     * @param id ID del reclamo
     * @return true si existe, false en caso contrario
     */
    boolean existsById(String id);

    /**
     * Busca reclamos recientes para un cliente específico.
     *
     * @param identityDocument documento de identidad del cliente
     * @return lista de reclamos
     */
    @Query("SELECT c FROM Claim c WHERE c.identityDocument = :identityDocument ORDER BY c.createdAt DESC")
    List<Claim> findRecentClaimsByIdentityDocument(@Param("identityDocument") String identityDocument);

    /**
     * Busca un reclamo por ID ignorando mayúsculas/minúsculas.
     *
     * @param id ID del reclamo
     * @return Optional con el reclamo si existe
     */
    @Query("SELECT c FROM Claim c WHERE LOWER(c.id) = LOWER(:id)")
    Optional<Claim> findByIdIgnoreCase(@Param("id") String id);

    /**
     * Cuenta el número total de reclamos por estado.
     *
     * @param status estado del reclamo
     * @return número de reclamos con el estado especificado
     */
    long countByStatus(Claim.ClaimStatus status);

    /**
     * Busca reclamos que contengan texto específico en la descripción.
     *
     * @param searchText texto a buscar en la descripción
     * @param pageable información de paginación
     * @return página de reclamos
     */
    @Query("SELECT c FROM Claim c WHERE LOWER(c.description) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<Claim> findByDescriptionContainingIgnoreCase(@Param("searchText") String searchText, Pageable pageable);
}