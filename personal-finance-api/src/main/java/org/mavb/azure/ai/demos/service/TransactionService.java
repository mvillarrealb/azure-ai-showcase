package org.mavb.azure.ai.demos.service;

import org.mavb.azure.ai.demos.dto.request.CreateTransactionDto;
import org.mavb.azure.ai.demos.dto.request.TransactionFilterDto;
import org.mavb.azure.ai.demos.dto.response.PaginatedResponseDto;
import org.mavb.azure.ai.demos.dto.response.TransactionDto;

/**
 * Interfaz del servicio para operaciones de transacciones
 */
public interface TransactionService {
    
    /**
     * Obtiene transacciones con filtros y paginación
     * @param filter Filtros de búsqueda
     * @return Respuesta paginada con transacciones
     */
    PaginatedResponseDto<TransactionDto> getTransactions(TransactionFilterDto filter);
    
    /**
     * Crea una nueva transacción
     * @param dto Datos de la transacción a crear
     * @return Transacción creada
     */
    TransactionDto createTransaction(CreateTransactionDto dto);
}