package org.mavb.azure.service;

import org.mavb.azure.dto.ProductDTO;
import org.mavb.azure.dto.ProductFilterDTO;
import org.mavb.azure.dto.ProductListResponseDTO;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for credit product operations.
 * Defines business logic methods for product management and searching.
 */
public interface ProductService {

    /**
     * Get all products with optional filtering and pagination.
     *
     * @param filter   Filter criteria for products
     * @param pageable Pagination information
     * @return Paginated list of products
     */
    ProductListResponseDTO getProducts(ProductFilterDTO filter, Pageable pageable);

    /**
     * Get a specific product by its ID.
     *
     * @param productId The unique identifier of the product
     * @return Product details
     * @throws org.mavb.azure.exception.ProductNotFoundException if product not found
     */
    ProductDTO getProductById(String productId);
}