package org.mavb.azure.ai.demos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.demos.dto.request.CreateTransactionDto;
import org.mavb.azure.ai.demos.dto.request.TransactionFilterDto;
import org.mavb.azure.ai.demos.dto.response.PaginatedResponseDto;
import org.mavb.azure.ai.demos.dto.response.TransactionDto;
import org.mavb.azure.ai.demos.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para operaciones de transacciones según tag "Transacciones" del OpenAPI
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class TransactionController {
    
    private final TransactionService transactionService;
    
    /**
     * Obtener todas las transacciones con filtros y paginación
     * Endpoint: GET /transactions
     * 
     * Devuelve una lista paginada de todas las transacciones financieras del usuario, 
     * con opciones de filtrado por fechas y categorías.
     */
    @GetMapping("/transactions")
    public ResponseEntity<PaginatedResponseDto<TransactionDto>> getTransactions(
            @Valid @ModelAttribute TransactionFilterDto filter) {
        
        log.debug("GET /api/v1/transactions - Filtros: {}", filter);
        
        PaginatedResponseDto<TransactionDto> response = transactionService.getTransactions(filter);
        
        log.debug("Retornando {} transacciones en la página {}", 
                 response.getData().size(),
                 response.getPagination().getCurrentPage());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Crear una nueva transacción
     * Endpoint: POST /transactions
     * 
     * Permite a los usuarios agregar una nueva transacción financiera al sistema, 
     * ya sea un gasto o un ingreso.
     */
    @PostMapping("/transactions")
    public ResponseEntity<TransactionDto> createTransaction(
            @Valid @RequestBody CreateTransactionDto dto) {
        
        log.debug("POST /api/v1/transactions - Creando transacción: {}", dto);
        
        TransactionDto created = transactionService.createTransaction(dto);
        
        log.info("Transacción creada exitosamente con ID: {}", created.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}