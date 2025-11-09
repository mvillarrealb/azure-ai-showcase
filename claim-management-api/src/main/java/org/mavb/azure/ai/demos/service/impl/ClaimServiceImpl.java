package org.mavb.azure.ai.demos.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.demos.dto.request.*;
import org.mavb.azure.ai.demos.dto.response.*;
import org.mavb.azure.ai.demos.exception.ClaimNotFoundException;
import org.mavb.azure.ai.demos.exception.InvalidFileException;
import org.mavb.azure.ai.demos.mapper.ClaimMapper;
import org.mavb.azure.ai.demos.model.Claim;
import org.mavb.azure.ai.demos.repository.ClaimRepository;
import org.mavb.azure.ai.demos.service.ClaimService;
import org.mavb.azure.ai.demos.service.OpenAIClaimService;
import org.mavb.azure.ai.demos.util.ExcelClaimParser;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de reclamos.
 * Contiene toda la lógica de negocio para operaciones CRUD.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimMapper claimMapper;
    private final Validator validator;
    private final ExcelClaimParser excelClaimParser;
    private final OpenAIClaimService aiClaimService;

    @Override
    @Transactional
    public ClaimDto createClaim(CreateClaimDto createClaimDto) {
        log.info("Creando nuevo reclamo para documento: {}", createClaimDto.getIdentityDocument());
        
        Claim claim = claimMapper.toEntity(createClaimDto);
        Claim savedClaim = claimRepository.save(claim);
        
        log.info("Reclamo creado exitosamente con ID: {}", savedClaim.getId());
        return claimMapper.toDto(savedClaim);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimListResponseDto getClaims(ClaimFilterDto filterDto) {
        log.info("Obteniendo lista de reclamos con filtros: {}", filterDto);
        
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(filterDto.getPage() - 1, filterDto.getLimit(), sort);
        
        Specification<Claim> spec = buildSpecification(filterDto);
        Page<Claim> claimsPage = claimRepository.findAll(spec, pageable);
        
        List<ClaimDto> claimDtos = claimMapper.toDtoList(claimsPage.getContent());
        
        PaginationDto pagination = PaginationDto.builder()
                .page(filterDto.getPage())
                .limit(filterDto.getLimit())
                .total(claimsPage.getTotalElements())
                .totalPages(claimsPage.getTotalPages())
                .build();
        
        return ClaimListResponseDto.builder()
                .data(claimDtos)
                .pagination(pagination)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimDto getClaimById(String id) {
        log.info("Obteniendo reclamo con ID: {}", id);
        
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException("El reclamo con ID " + id + " no existe"));
        
        return claimMapper.toDto(claim);
    }

    @Override
    @Transactional
    public ClaimDto resolveClaim(String id, ResolveClaimDto resolveClaimDto) {
        log.info("Resolviendo reclamo con ID: {}", id);
        
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException("El reclamo con ID " + id + " no existe"));
        
        claim.setStatus(Claim.ClaimStatus.resolved);
        claim.setComments(resolveClaimDto.getComments());
        
        Claim savedClaim = claimRepository.save(claim);
        
        log.info("Reclamo {} resuelto exitosamente", id);
        return claimMapper.toDto(savedClaim);
    }

    @Override
    @Transactional
    public Mono<ImportResponseDto> importClaims(FilePart filePart) {
        log.info("Iniciando importación de reclamos desde archivo: {}", filePart.filename());
        
        return validateFileReactive(filePart)
                .then(DataBufferUtils.join(filePart.content())
                        .map(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);
                            return bytes;
                        }))
                .flatMap(this::processExcelFileReactive);
    }

    /**
     * Construye la especificación JPA para filtros de búsqueda.
     */
    private Specification<Claim> buildSpecification(ClaimFilterDto filterDto) {
        Specification<Claim> spec = null;
        
        if (filterDto.getIdentityDocument() != null && !filterDto.getIdentityDocument().isEmpty()) {
            spec = (root, query, cb) ->
                    cb.equal(root.get("identityDocument"), filterDto.getIdentityDocument());
        }
        
        if (filterDto.getStatus() != null && !filterDto.getStatus().isEmpty()) {
            try {
                Claim.ClaimStatus status = Claim.ClaimStatus.valueOf(filterDto.getStatus());
                Specification<Claim> statusSpec = (root, query, cb) -> 
                        cb.equal(root.get("status"), status);
                spec = spec == null ? statusSpec : spec.and(statusSpec);
            } catch (IllegalArgumentException e) {
                log.warn("Estado inválido proporcionado: {}", filterDto.getStatus());
            }
        }
        
        return spec;
    }

    /**
     * Valida el archivo de importación de forma reactiva.
     */
    private Mono<Void> validateFileReactive(FilePart filePart) {
        return Mono.fromCallable(() -> {
            if (filePart == null) {
                throw new InvalidFileException("El archivo es requerido");
            }
            
            String filename = filePart.filename();
            if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
                throw new InvalidFileException("El archivo debe ser un Excel válido (.xlsx o .xls)");
            }
            
            return null;
        }).then();
    }

    /**
     * Procesa el archivo Excel de forma reactiva.
     */
    private Mono<ImportResponseDto> processExcelFileReactive(byte[] fileBytes) {
        return Mono.fromCallable(() -> excelClaimParser.parseExcelFromBytes(fileBytes))
                .flatMap(aiClaimService::getReasonDataFromModelReactive)

                .map(enhancedClaimDtos -> {
                    List<Claim> successfulClaims = new ArrayList<>();
                    List<ImportResponseDto.ImportErrorDto> errors = new ArrayList<>();
                    
                    for (ImportClaimDto importClaimDto : enhancedClaimDtos) {
                        try {
                            Set<ConstraintViolation<ImportClaimDto>> violations = validator.validate(importClaimDto);
                            if (!violations.isEmpty()) {
                                String errorMessage = violations.stream()
                                        .map(ConstraintViolation::getMessage)
                                        .collect(Collectors.joining(", "));
                                errors
                                        .add(ImportResponseDto.ImportErrorDto.builder()
                                        .row(importClaimDto.getRowNumber())
                                        .error(errorMessage)
                                        .build());
                                continue;
                            }
                            
                            Claim claim = claimMapper.toEntity(importClaimDto);
                            Claim savedClaim = claimRepository.save(claim);
                            successfulClaims.add(savedClaim);
                            
                        } catch (Exception e) {
                            log.error("Error procesando fila {}: {}", importClaimDto.getRowNumber(), e.getMessage());
                            errors.add(ImportResponseDto.ImportErrorDto.builder()
                                    .row(importClaimDto.getRowNumber())
                                    .error("Error inesperado: " + e.getMessage())
                                    .build());
                        }
                    }
                    
                    log.info("Importación completada. Exitosos: {}, Errores: {}", 
                            successfulClaims.size(), errors.size());
                    
                    return ImportResponseDto.builder()
                            .message("Importación completada exitosamente")
                            .totalProcessed(enhancedClaimDtos.size())
                            .successful(successfulClaims.size())
                            .failed(errors.size())
                            .claimsCreated(claimMapper.toImportedClaimDtoList(successfulClaims))
                            .errors(errors)
                            .build();
                });
    }
}