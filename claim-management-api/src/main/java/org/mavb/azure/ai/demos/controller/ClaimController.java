package org.mavb.azure.ai.demos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.demos.dto.request.*;
import org.mavb.azure.ai.demos.dto.response.*;
import org.mavb.azure.ai.demos.service.ClaimService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para la gestión de reclamos.
 */
@RestController
@RequestMapping("/claims")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ClaimController {

    private final ClaimService claimService;

    /**
     * Crea un nuevo reclamo.
     */
    @PostMapping
    public ResponseEntity<ClaimDto> createClaim(@Valid @RequestBody CreateClaimDto createClaimDto) {
        log.info("Solicitud para crear reclamo para documento: {}", createClaimDto.getIdentityDocument());
        
        ClaimDto createdClaim = claimService.createClaim(createClaimDto);
        
        log.info("Reclamo creado exitosamente con ID: {}", createdClaim.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClaim);
    }

    /**
     * Obtiene lista paginada de reclamos con filtros opcionales.
     */
    @GetMapping
    public ResponseEntity<ClaimListResponseDto> getClaims(
            @RequestParam(required = false) String identityDocument,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(required = false) String status) {
        
        log.info("Solicitud para obtener reclamos - Documento: {}, Página: {}, Límite: {}, Estado: {}", 
                identityDocument, page, limit, status);
        
        ClaimFilterDto filterDto = ClaimFilterDto.builder()
                .identityDocument(identityDocument)
                .page(page)
                .limit(limit)
                .status(status)
                .build();
        
        ClaimListResponseDto response = claimService.getClaims(filterDto);
        
        log.info("Obtenidos {} reclamos de un total de {}", 
                response.getData().size(), response.getPagination().getTotal());
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un reclamo específico por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClaimDto> getClaimById(@PathVariable String id) {
        log.info("Solicitud para obtener reclamo con ID: {}", id);
        
        ClaimDto claim = claimService.getClaimById(id);
        
        log.info("Reclamo obtenido exitosamente: {}", id);
        return ResponseEntity.ok(claim);
    }

    /**
     * Resuelve un reclamo cambiando su estado a 'resolved'.
     */
    @PostMapping("/{id}/resolve")
    public ResponseEntity<ClaimDto> resolveClaim(
            @PathVariable String id,
            @Valid @RequestBody ResolveClaimDto resolveClaimDto) {
        
        log.info("Solicitud para resolver reclamo con ID: {}", id);
        
        ClaimDto resolvedClaim = claimService.resolveClaim(id, resolveClaimDto);
        
        log.info("Reclamo {} resuelto exitosamente", id);
        return ResponseEntity.ok(resolvedClaim);
    }

    /**
     * Importa reclamos desde archivo Excel usando WebFlux.
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ImportResponseDto>> importClaims(@RequestPart("file") Mono<FilePart> filePartMono) {
        log.info("Solicitud para importar reclamos desde archivo usando WebFlux");
        
        return filePartMono
                .doOnNext(filePart -> log.info("Procesando archivo: {}", filePart.filename()))
                .flatMap(claimService::importClaims)
                .doOnSuccess(response -> log.info("Importación completada - Procesados: {}, Exitosos: {}, Errores: {}", 
                        response.getTotalProcessed(), response.getSuccessful(), response.getFailed()))
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .doOnError(error -> log.error("Error durante la importación: {}", error.getMessage()));
    }
}