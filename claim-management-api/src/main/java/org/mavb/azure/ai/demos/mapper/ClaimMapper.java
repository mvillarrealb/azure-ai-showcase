package org.mavb.azure.ai.demos.mapper;

import org.mapstruct.*;
import org.mavb.azure.ai.demos.dto.request.*;
import org.mavb.azure.ai.demos.dto.response.*;
import org.mavb.azure.ai.demos.model.Claim;

import java.util.List;

/**
 * Mapper de MapStruct para conversiones entre entidades Claim y DTOs.
 * Se genera automáticamente en tiempo de compilación.
 */
@Mapper(componentModel = "spring")
public interface ClaimMapper {

    /**
     * Convierte una entidad Claim a ClaimDto.
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    ClaimDto toDto(Claim claim);

    /**
     * Convierte una lista de entidades Claim a lista de ClaimDto.
     */
    List<ClaimDto> toDtoList(List<Claim> claims);

    /**
     * Convierte CreateClaimDto a entidad Claim.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Claim toEntity(CreateClaimDto createClaimDto);

    /**
     * Convierte ImportClaimDto a entidad Claim.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Claim toEntity(ImportClaimDto importClaimDto);

    /**
     * Convierte entidad Claim a ImportedClaimDto.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "identityDocument", source = "identityDocument")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "subReason", source = "subReason")
    ImportResponseDto.ImportedClaimDto toImportedClaimDto(Claim claim);

    /**
     * Convierte lista de entidades Claim a lista de ImportedClaimDto.
     */
    List<ImportResponseDto.ImportedClaimDto> toImportedClaimDtoList(List<Claim> claims);

    /**
     * Método auxiliar para convertir enum ClaimStatus a String.
     */
    @Named("statusToString")
    default String statusToString(Claim.ClaimStatus status) {
        return status != null ? status.name() : null;
    }

    /**
     * Método auxiliar para convertir String a enum ClaimStatus.
     */
    @Named("stringToStatus")
    default Claim.ClaimStatus stringToStatus(String status) {
        if (status == null || status.isEmpty()) {
            return Claim.ClaimStatus.open; // Default value
        }
        try {
            return Claim.ClaimStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return Claim.ClaimStatus.open; // Default value on error
        }
    }
}