package org.mavb.azure.ai.repository;

import org.mavb.azure.ai.entity.RankEntity;
import org.springframework.data.jpa.domain.Specification;

/**
 * Especificaciones JPA para construir consultas dinámicas de RankEntity.
 * Sigue el patrón estándar establecido en TransactionSpecifications y ClaimSpecifications.
 */
public class RankSpecifications {

    /**
     * Filtra rangos activos únicamente.
     */
    public static Specification<RankEntity> isActive() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.isTrue(root.get("active"));
    }

    /**
     * Filtra rangos por nombre (búsqueda parcial, case insensitive).
     *
     * @param name nombre a buscar (puede ser parcial)
     * @return specification para filtrar por nombre
     */
    public static Specification<RankEntity> hasNameContaining(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return null;
            }
            String likePattern = "%" + name.toLowerCase() + "%";
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")), 
                likePattern
            );
        };
    }

    /**
     * Filtra rangos por descripción (búsqueda parcial, case insensitive).
     *
     * @param description texto a buscar en la descripción
     * @return specification para filtrar por descripción
     */
    public static Specification<RankEntity> hasDescriptionContaining(String description) {
        return (root, query, criteriaBuilder) -> {
            if (description == null || description.trim().isEmpty()) {
                return null;
            }
            String likePattern = "%" + description.toLowerCase() + "%";
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("description")), 
                likePattern
            );
        };
    }

    /**
     * Filtra rangos por nombre o descripción (búsqueda global).
     *
     * @param searchTerm término de búsqueda para nombre o descripción
     * @return specification para búsqueda global
     */
    public static Specification<RankEntity> hasNameOrDescriptionContaining(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return null;
            }
            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern)
            );
        };
    }

    /**
     * Filtra rangos por ID exacto.
     *
     * @param id ID del rango
     * @return specification para filtrar por ID
     */
    public static Specification<RankEntity> hasId(String id) {
        return (root, query, criteriaBuilder) -> {
            if (id == null || id.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("id"), id);
        };
    }

    /**
     * Ordena por nombre ascendente.
     */
    public static Specification<RankEntity> orderByNameAsc() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.asc(root.get("name")));
            return null;
        };
    }

    /**
     * Ordena por fecha de creación descendente (más recientes primero).
     */
    public static Specification<RankEntity> orderByCreatedAtDesc() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
            return null;
        };
    }
}