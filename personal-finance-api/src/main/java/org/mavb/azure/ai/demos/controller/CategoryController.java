package org.mavb.azure.ai.demos.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.demos.dto.response.CategoryDto;
import org.mavb.azure.ai.demos.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador para operaciones de categorías según tag "Categorías" del OpenAPI
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    
    private final CategoryService categoryService;
    
    /**
     * Obtener todas las categorías
     * Endpoint: GET /categories
     * 
     * Devuelve una lista completa de categorías de gastos e ingresos 
     * disponibles para clasificar transacciones financieras.
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getCategories() {
        log.debug("GET /api/v1/categories - Obteniendo todas las categorías");
        
        List<CategoryDto> categories = categoryService.getAllCategories();
        
        log.debug("Retornando {} categorías", categories.size());
        
        return ResponseEntity.ok(categories);
    }
}