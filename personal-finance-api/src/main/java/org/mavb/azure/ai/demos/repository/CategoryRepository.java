package org.mavb.azure.ai.demos.repository;

import org.mavb.azure.ai.demos.model.Category;
import org.mavb.azure.ai.demos.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de Category
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    
    /**
     * Busca categorías por tipo (INCOME o EXPENSE)
     */
    List<Category> findByType(CategoryType type);
    
    /**
     * Busca una categoría por nombre (case insensitive)
     */
    Optional<Category> findByNameIgnoreCase(String name);
    
    /**
     * Verifica si existe una categoría con el nombre dado
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Busca categorías cuyo nombre contenga el texto dado (case insensitive)
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Category> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);
    
    /**
     * Obtiene el conteo de categorías por tipo
     */
    @Query("SELECT c.type, COUNT(c) FROM Category c GROUP BY c.type")
    List<Object[]> countCategoriesByType();
}