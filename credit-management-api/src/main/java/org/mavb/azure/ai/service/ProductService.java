package org.mavb.azure.ai.service;

import org.mavb.azure.ai.dto.request.CreateProductDTO;
import org.mavb.azure.ai.dto.request.ProductFilterDTO;
import org.mavb.azure.ai.dto.response.ProductDTO;
import org.mavb.azure.ai.dto.response.ProductListResponseDTO;
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
     * @throws org.mavb.azure.ai.exception.ProductNotFoundException if product not found
     */
    ProductDTO getProductById(String productId);

    /**
     * Create a new credit product.
     * The product will be automatically synchronized with Azure AI Search.
     *
     * @param createProductDTO Product creation data
     * @return Created product details
     * @throws org.mavb.azure.ai.exception.ProductAlreadyExistsException if product ID already exists
     */
    ProductDTO createProduct(CreateProductDTO createProductDTO);
}