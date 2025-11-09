package org.mavb.azure.ai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.dto.request.ProductFilterDTO;
import org.mavb.azure.ai.dto.response.ProductDTO;
import org.mavb.azure.ai.dto.response.ProductListResponseDTO;
import org.mavb.azure.ai.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * REST Controller for credit product management.
 * Handles HTTP requests for product listing and detailed product information.
 * 
 * Base path: /products
 * 
 * Endpoints:
 * - GET /products - Lista productos crediticios con filtros opcionales
 * - GET /products/{productId} - Obtiene detalles de un producto específico
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    /**
     * Obtener lista de productos crediticios.
     * Devuelve una lista completa de productos crediticios disponibles con filtros opcionales.
     *
     * @param category   Filtrar por categoría de producto (opcional)
     * @param currency   Filtrar por moneda (S/ o USD) (opcional)
     * @param minAmount  Monto mínimo requerido (opcional)
     * @param maxAmount  Monto máximo requerido (opcional)
     * @param page       Número de página (por defecto 0)
     * @param size       Tamaño de página (por defecto 20)
     * @param sort       Campo de ordenación (por defecto 'name')
     * @return Lista paginada de productos crediticios
     */
    @GetMapping
    public ResponseEntity<ProductListResponseDTO> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort) {

        log.debug("GET /products - category: {}, currency: {}, minAmount: {}, maxAmount: {}, page: {}, size: {}",
                category, currency, minAmount, maxAmount, page, size);

        ProductFilterDTO filter = ProductFilterDTO.builder()
                .category(category)
                .currency(currency)
                .minAmount(minAmount)
                .maxAmount(maxAmount)
                .build();

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        ProductListResponseDTO response = productService.getProducts(filter, pageable);

        log.info("Retrieved {} products out of {} total", 
                response.getData().size(), response.getTotal());

        return ResponseEntity.ok(response);
    }

    /**
     * Obtener detalles de un producto crediticio específico.
     * Devuelve información completa de un producto crediticio por su ID.
     *
     * @param productId ID único del producto crediticio
     * @return Detalles del producto crediticio
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable String productId) {
        log.debug("GET /products/{} - Fetching product details", productId);

        ProductDTO product = productService.getProductById(productId);

        log.info("Successfully retrieved product: {}", product.getName());
        
        return ResponseEntity.ok(product);
    }
}