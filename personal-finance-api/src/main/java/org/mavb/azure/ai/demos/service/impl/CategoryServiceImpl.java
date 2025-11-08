package org.mavb.azure.ai.demos.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.demos.dto.response.CategoryDto;
import org.mavb.azure.ai.demos.mapper.CategoryMapper;
import org.mavb.azure.ai.demos.model.Category;
import org.mavb.azure.ai.demos.repository.CategoryRepository;
import org.mavb.azure.ai.demos.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de categorías
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        log.debug("Obteniendo todas las categorías");
        
        List<Category> categories = categoryRepository.findAll();
        
        log.debug("Se encontraron {} categorías", categories.size());
        
        return categoryMapper.toDtoList(categories);
    }
}