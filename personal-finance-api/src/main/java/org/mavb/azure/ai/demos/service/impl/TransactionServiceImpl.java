package org.mavb.azure.ai.demos.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.demos.dto.request.CreateTransactionDto;
import org.mavb.azure.ai.demos.dto.request.TransactionFilterDto;
import org.mavb.azure.ai.demos.dto.response.PaginatedResponseDto;
import org.mavb.azure.ai.demos.dto.response.TransactionDto;
import org.mavb.azure.ai.demos.exception.NotFoundException;
import org.mavb.azure.ai.demos.exception.ValidationException;
import org.mavb.azure.ai.demos.mapper.PaginationMapper;
import org.mavb.azure.ai.demos.mapper.TransactionMapper;
import org.mavb.azure.ai.demos.model.Category;
import org.mavb.azure.ai.demos.model.Transaction;
import org.mavb.azure.ai.demos.repository.CategoryRepository;
import org.mavb.azure.ai.demos.repository.TransactionRepository;
import org.mavb.azure.ai.demos.repository.TransactionSpecifications;
import org.mavb.azure.ai.demos.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementación del servicio de transacciones
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;
    private final PaginationMapper paginationMapper;
    
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDto<TransactionDto> getTransactions(TransactionFilterDto filter) {
        log.debug("Obteniendo transacciones con filtros: {}", filter);
        
        // Validaciones
        validateFilter(filter);
        
        // Construir especificación de consulta
        Specification<Transaction> spec = buildSpecification(filter);
        
        // Configurar paginación
        Pageable pageable = buildPageable(filter);
        
        // Ejecutar consulta
        Page<Transaction> transactionsPage = transactionRepository.findAll(spec, pageable);
        
        log.debug("Se encontraron {} transacciones en la página {} de {}", 
                 transactionsPage.getNumberOfElements(), 
                 transactionsPage.getNumber() + 1, 
                 transactionsPage.getTotalPages());
        
        // Mapear entidades a DTOs
        Page<TransactionDto> dtoPage = transactionsPage.map(transactionMapper::toDto);
        
        // Crear respuesta paginada
        return paginationMapper.toPaginatedResponse(dtoPage);
    }
    
    @Override
    @Transactional
    public TransactionDto createTransaction(CreateTransactionDto dto) {
        log.debug("Creando nueva transacción: {}", dto);
        
        // Validaciones de negocio
        validateCreateTransaction(dto);
        
        // Obtener la categoría
        Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Categoría no encontrada: " + dto.getCategoryId()));
        
        // Mapear DTO a entidad
        Transaction transaction = transactionMapper.toEntity(dto);
        transaction.setCategory(category);
        
        // Guardar en base de datos
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        log.info("Transacción creada exitosamente con ID: {}", savedTransaction.getId());
        
        // Mapear a DTO de respuesta
        return transactionMapper.toDto(savedTransaction);
    }
    
    /**
     * Valida los filtros de búsqueda
     */
    private void validateFilter(TransactionFilterDto filter) {
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            if (filter.getStartDate().isAfter(filter.getEndDate())) {
                throw new ValidationException("La fecha de inicio debe ser anterior a la fecha de fin");
            }
        }
    }
    
    /**
     * Valida los datos de creación de transacción
     */
    private void validateCreateTransaction(CreateTransactionDto dto) {
        // Validar que la fecha no sea futura
        if (dto.getDate().isAfter(LocalDateTime.now())) {
            throw new ValidationException("La fecha de la transacción no puede ser futura");
        }
        
        // Validar que el monto no sea cero
        if (dto.getAmount().compareTo(java.math.BigDecimal.ZERO) == 0) {
            throw new ValidationException("El monto de la transacción no puede ser cero");
        }
    }
    
    /**
     * Construye la especificación de consulta basada en los filtros
     */
    private Specification<Transaction> buildSpecification(TransactionFilterDto filter) {
        Specification<Transaction> spec = Specification.where(null);
        
        // Filtro por categoría
        if (filter.getCategoryId() != null && !filter.getCategoryId().trim().isEmpty()) {
            spec = spec.and(TransactionSpecifications.hasCategory(filter.getCategoryId()));
        }
        
        // Filtro por rango de fechas
        if (filter.getStartDate() != null) {
            spec = spec.and(TransactionSpecifications.hasDateAfterOrEqual(filter.getStartDate()));
        }
        
        if (filter.getEndDate() != null) {
            spec = spec.and(TransactionSpecifications.hasDateBeforeOrEqual(filter.getEndDate()));
        }
        
        return spec;
    }
    
    /**
     * Construye la configuración de paginación
     */
    private Pageable buildPageable(TransactionFilterDto filter) {
        // Convertir de 1-indexed (DTO) a 0-indexed (Spring Data)
        int pageNumber = Math.max(0, filter.getPage() - 1);
        int pageSize = filter.getLimit();
        
        // Ordenar por fecha descendente (más recientes primero)
        Sort sort = Sort.by(Sort.Direction.DESC, "date");
        
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}