package org.mavb.azure.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.dto.ProductDTO;
import org.mavb.azure.dto.ProductFilterDTO;
import org.mavb.azure.dto.ProductListResponseDTO;
import org.mavb.azure.entity.CreditProductEntity;
import org.mavb.azure.exception.ProductNotFoundException;
import org.mavb.azure.mapper.CreditProductMapper;
import org.mavb.azure.repository.CreditProductRepository;
import org.mavb.azure.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .currentPage(productPage.getNumber() + 1) // Convert from 0-based to 1-based
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
}