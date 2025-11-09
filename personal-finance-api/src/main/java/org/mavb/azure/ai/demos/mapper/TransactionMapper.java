package org.mavb.azure.ai.demos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mavb.azure.ai.demos.dto.request.CreateTransactionDto;
import org.mavb.azure.ai.demos.dto.response.TransactionDto;
import org.mavb.azure.ai.demos.model.Transaction;

import java.util.List;

/**
 * Mapper para conversiones entre Transaction entity y DTOs
 */
@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface TransactionMapper {
    
    /**
     * Convierte Transaction entity a TransactionDto
     */
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.type", target = "categoryType", qualifiedByName = "categoryTypeToString")
    TransactionDto toDto(Transaction transaction);
    
    /**
     * Convierte lista de Transaction entities a lista de TransactionDto
     */
    List<TransactionDto> toDtoList(List<Transaction> transactions);
    
    /**
     * Convierte CreateTransactionDto a Transaction entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true) // Se asignará en el servicio
    Transaction toEntity(CreateTransactionDto dto);
    
    /**
     * Actualiza una transaction existente con datos del DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true) // Se maneja en el servicio
    void updateEntity(@MappingTarget Transaction transaction, CreateTransactionDto dto);
}