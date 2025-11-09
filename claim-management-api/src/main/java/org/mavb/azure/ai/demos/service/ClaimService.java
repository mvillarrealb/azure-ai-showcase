package org.mavb.azure.ai.demos.service;

import org.mavb.azure.ai.demos.dto.request.*;
import org.mavb.azure.ai.demos.dto.response.*;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

/**
 * Interfaz de servicio para la gestión de reclamos.
 * Define todos los métodos de negocio requeridos por el API.
 */
public interface ClaimService {

    /**
     * Crea un nuevo reclamo.
     *
     * @param createClaimDto DTO con los datos del reclamo a crear
     * @return ClaimDto con el reclamo creado
     */
    ClaimDto createClaim(CreateClaimDto createClaimDto);

    /**
     * Obtiene una lista paginada de reclamos con filtros opcionales.
     *
     * @param filterDto DTO con los filtros de búsqueda
     * @return ClaimListResponseDto con la lista de reclamos y paginación
     */
    ClaimListResponseDto getClaims(ClaimFilterDto filterDto);

    /**
     * Obtiene un reclamo específico por su ID.
     *
     * @param id ID del reclamo
     * @return ClaimDto con los datos del reclamo
     * @throws org.mavb.azure.ai.demos.exception.ClaimNotFoundException si el reclamo no existe
     */
    ClaimDto getClaimById(String id);

    /**
     * Resuelve un reclamo cambiando su estado a resuelto.
     *
     * @param id ID del reclamo a resolver
     * @param resolveClaimDto DTO con los comentarios de resolución
     * @return ClaimDto con el reclamo actualizado
     * @throws org.mavb.azure.ai.demos.exception.ClaimNotFoundException si el reclamo no existe
     */
    ClaimDto resolveClaim(String id, ResolveClaimDto resolveClaimDto);

    /**
     * Importa reclamos desde un archivo Excel usando WebFlux.
     *
     * @param filePart FilePart reactivo con el archivo Excel
     * @return Mono<ImportResponseDto> con el resultado de la importación
     * @throws org.mavb.azure.ai.demos.exception.InvalidFileException si el archivo no es válido
     */
    Mono<ImportResponseDto> importClaims(FilePart filePart);
}