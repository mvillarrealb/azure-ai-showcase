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
        
        validateFilter(filter);
        
        Specification<Transaction> spec = buildSpecification(filter);
        
        Pageable pageable = buildPageable(filter);
        
        Page<Transaction> transactionsPage = transactionRepository.findAll(spec, pageable);
        
        log.debug("Se encontraron {} transacciones en la página {} de {}", 
                 transactionsPage.getNumberOfElements(), 
                 transactionsPage.getNumber() + 1, 
                 transactionsPage.getTotalPages());
        
        Page<TransactionDto> dtoPage = transactionsPage.map(transactionMapper::toDto);
        
        return paginationMapper.toPaginatedResponse(dtoPage);
    }
    
    @Override
    @Transactional
    public TransactionDto createTransaction(CreateTransactionDto dto) {
        log.debug("Creando nueva transacción: {}", dto);
        
        validateCreateTransaction(dto);
        
        Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Categoría no encontrada: " + dto.getCategoryId()));
        
        Transaction transaction = transactionMapper.toEntity(dto);
        transaction.setCategory(category);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        log.info("Transacción creada exitosamente con ID: {}", savedTransaction.getId());
        
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
        if (dto.getDate().isAfter(LocalDateTime.now())) {
            throw new ValidationException("La fecha de la transacción no puede ser futura");
        }
        
        if (dto.getAmount().compareTo(java.math.BigDecimal.ZERO) == 0) {
            throw new ValidationException("El monto de la transacción no puede ser cero");
        }
    }
    
    /**
     * Construye la especificación de consulta basada en los filtros
     */
    private Specification<Transaction> buildSpecification(TransactionFilterDto filter) {
        Specification<Transaction> spec = Specification.where(null);
        
        if (filter.getCategoryId() != null && !filter.getCategoryId().trim().isEmpty()) {
            spec = spec.and(TransactionSpecifications.hasCategory(filter.getCategoryId()));
        }
        
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
        int pageNumber = Math.max(0, filter.getPage() - 1);
        int pageSize = filter.getLimit();
        
        Sort sort = Sort.by(Sort.Direction.DESC, "date");
        
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}