package org.mavb.azure.ai.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.dto.request.CreateProductDTO;
import org.mavb.azure.ai.dto.request.ProductFilterDTO;
import org.mavb.azure.ai.dto.response.ProductDTO;
import org.mavb.azure.ai.dto.response.ProductListResponseDTO;
import org.mavb.azure.ai.entity.CreditProductEntity;
import org.mavb.azure.ai.exception.ProductAlreadyExistsException;
import org.mavb.azure.ai.exception.ProductNotFoundException;
import org.mavb.azure.ai.mapper.CreditProductMapper;
import org.mavb.azure.ai.repository.CreditProductRepository;
import org.mavb.azure.ai.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of ProductService interface.
 * Provides business logic for credit product operations including filtering and searching.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final CreditProductRepository creditProductRepository;
    private final CreditProductMapper creditProductMapper;

    @Override
    public ProductListResponseDTO getProducts(ProductFilterDTO filter, Pageable pageable) {
        log.debug("Fetching products with filter: {} and pagination: {}", filter, pageable);

        Page<CreditProductEntity> productPage = creditProductRepository.findWithFilters(
                filter.getCategory(),
                filter.getCurrency(),
                filter.getMinAmount(),
                filter.getMaxAmount(),
                pageable
        );

        List<ProductDTO> productDTOs = creditProductMapper.toDtoList(productPage.getContent());

        ProductListResponseDTO response = ProductListResponseDTO.builder()
                .data(productDTOs)
                .total((int) productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .currentPage(productPage.getNumber() + 1)
                .build();

        log.debug("Found {} products, total pages: {}", productDTOs.size(), productPage.getTotalPages());
        return response;
    }

    @Override
    public ProductDTO getProductById(String productId) {
        log.debug("Fetching product with ID: {}", productId);

        CreditProductEntity product = creditProductRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", productId);
                    return new ProductNotFoundException("El producto con ID " + productId + " no existe");
                });

        ProductDTO productDTO = creditProductMapper.toDto(product);
        log.debug("Successfully found product: {}", product.getName());
        
        return productDTO;
    }

    @Override
    @Transactional
    public ProductDTO createProduct(CreateProductDTO createProductDTO) {
        log.debug("Creating new product with ID: {}", createProductDTO.getId());

        // Check if product already exists
        if (creditProductRepository.existsById(createProductDTO.getId())) {
            log.warn("Product with ID {} already exists", createProductDTO.getId());
            throw new ProductAlreadyExistsException(
                "El producto con ID " + createProductDTO.getId() + " ya existe"
            );
        }

        // Create entity from DTO
        CreditProductEntity product = CreditProductEntity.builder()
                .id(createProductDTO.getId())
                .name(createProductDTO.getName())
                .description(createProductDTO.getDescription())
                .category(createProductDTO.getCategory())
                .subcategory(createProductDTO.getSubcategory())
                .minimumAmount(createProductDTO.getMinimumAmount())
                .maximumAmount(createProductDTO.getMaximumAmount())
                .currency(createProductDTO.getCurrency())
                .term(createProductDTO.getTerm())
                .minimumRate(createProductDTO.getMinimumRate())
                .maximumRate(createProductDTO.getMaximumRate())
                .requirements(createProductDTO.getRequirements())
                .features(createProductDTO.getFeatures())
                .benefits(createProductDTO.getBenefits())
                .active(createProductDTO.getActive())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Save the product (will trigger ProductSyncListener automatically)
        CreditProductEntity savedProduct = creditProductRepository.save(product);

        // Convert to DTO and return
        ProductDTO productDTO = creditProductMapper.toDto(savedProduct);
        
        log.info("Successfully created product: {} with ID: {}", savedProduct.getName(), savedProduct.getId());
        return productDTO;
    }
}