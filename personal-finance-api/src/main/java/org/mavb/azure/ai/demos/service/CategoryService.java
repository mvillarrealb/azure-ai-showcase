package org.mavb.azure.ai.demos.service;

import org.mavb.azure.ai.demos.dto.response.CategoryDto;

import java.util.List;

/**
 * Interfaz del servicio para operaciones de categorías
 */
public interface CategoryService {
    
    /**
     * Obtiene todas las categorías disponibles
     * @return Lista de categorías
     */
    List<CategoryDto> getAllCategories();
}