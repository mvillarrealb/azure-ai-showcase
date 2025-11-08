package org.mavb.azure.ai.demos.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mavb.azure.ai.demos.dto.*;
import org.mavb.azure.ai.demos.exception.ClaimNotFoundException;
import org.mavb.azure.ai.demos.exception.InvalidFileException;
import org.mavb.azure.ai.demos.mapper.ClaimMapper;
import org.mavb.azure.ai.demos.model.Claim;
import org.mavb.azure.ai.demos.repository.ClaimRepository;
import org.mavb.azure.ai.demos.service.ClaimService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de reclamos.
 * Contiene toda la lógica de negocio para operaciones CRUD y procesamiento de archivos Excel.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimMapper claimMapper;
    private final Validator validator;

    @Override
    @Transactional
    public ClaimDto createClaim(CreateClaimDto createClaimDto) {
        log.info("Creando nuevo reclamo para documento: {}", createClaimDto.getIdentityDocument());
        
        // Convertir DTO a entidad
        Claim claim = claimMapper.toEntity(createClaimDto);
        
        // Guardar la entidad
        Claim savedClaim = claimRepository.save(claim);
        
        log.info("Reclamo creado exitosamente con ID: {}", savedClaim.getId());
        return claimMapper.toDto(savedClaim);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimListResponseDto getClaims(ClaimFilterDto filterDto) {
        log.info("Obteniendo lista de reclamos con filtros: {}", filterDto);
        
        // Crear Pageable
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(filterDto.getPage() - 1, filterDto.getLimit(), sort);
        
        // Aplicar filtros usando Specification
        Specification<Claim> spec = buildSpecification(filterDto);
        Page<Claim> claimsPage = claimRepository.findAll(spec, pageable);
        
        // Mapear resultados
        List<ClaimDto> claimDtos = claimMapper.toDtoList(claimsPage.getContent());
        
        // Crear información de paginación
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
        
        // Actualizar estado y comentarios
        claim.setStatus(Claim.ClaimStatus.resolved);
        claim.setComments(resolveClaimDto.getComments());
        
        Claim savedClaim = claimRepository.save(claim);
        
        log.info("Reclamo {} resuelto exitosamente", id);
        return claimMapper.toDto(savedClaim);
    }

    @Override
    @Transactional
    public ImportResponseDto importClaims(MultipartFile file) {
        log.info("Iniciando importación de reclamos desde archivo: {}", file.getOriginalFilename());
        
        validateFile(file);
        
        List<ImportClaimDto> importClaimDtos = parseExcelFile(file);
        
        List<Claim> successfulClaims = new ArrayList<>();
        List<ImportResponseDto.ImportErrorDto> errors = new ArrayList<>();
        
        for (ImportClaimDto importClaimDto : importClaimDtos) {
            try {
                // Validar DTO
                Set<ConstraintViolation<ImportClaimDto>> violations = validator.validate(importClaimDto);
                if (!violations.isEmpty()) {
                    String errorMessage = violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.joining(", "));
                    errors.add(ImportResponseDto.ImportErrorDto.builder()
                            .row(importClaimDto.getRowNumber())
                            .error(errorMessage)
                            .build());
                    continue;
                }
                
                // Crear y guardar entidad
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
                .totalProcessed(importClaimDtos.size())
                .successful(successfulClaims.size())
                .failed(errors.size())
                .claimsCreated(claimMapper.toImportedClaimDtoList(successfulClaims))
                .errors(errors)
                .build();
    }

    /**
     * Construye la especificación JPA para filtros de búsqueda.
     */
    private Specification<Claim> buildSpecification(ClaimFilterDto filterDto) {
        Specification<Claim> spec = Specification.where(null);
        
        if (filterDto.getIdentityDocument() != null && !filterDto.getIdentityDocument().isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                    cb.equal(root.get("identityDocument"), filterDto.getIdentityDocument()));
        }
        
        if (filterDto.getStatus() != null && !filterDto.getStatus().isEmpty()) {
            try {
                Claim.ClaimStatus status = Claim.ClaimStatus.valueOf(filterDto.getStatus());
                spec = spec.and((root, query, cb) -> 
                        cb.equal(root.get("status"), status));
            } catch (IllegalArgumentException e) {
                log.warn("Estado inválido proporcionado: {}", filterDto.getStatus());
            }
        }
        
        return spec;
    }

    /**
     * Valida el archivo de importación.
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("El archivo es requerido");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new InvalidFileException("El archivo debe ser un Excel válido (.xlsx o .xls)");
        }
        
        // Validar tamaño máximo (10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new InvalidFileException("El archivo es demasiado grande. Tamaño máximo: 10MB");
        }
    }

    /**
     * Parsea el archivo Excel y convierte las filas a DTOs.
     */
    private List<ImportClaimDto> parseExcelFile(MultipartFile file) {
        List<ImportClaimDto> importClaimDtos = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Saltar la primera fila (headers)
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;
                
                try {
                    ImportClaimDto dto = parseRowToDto(row, rowIndex + 1);
                    if (dto != null) {
                        importClaimDtos.add(dto);
                    }
                } catch (Exception e) {
                    log.error("Error parseando fila {}: {}", rowIndex + 1, e.getMessage());
                    // Continúa con la siguiente fila
                }
            }
        } catch (IOException e) {
            throw new InvalidFileException("Error leyendo el archivo Excel: " + e.getMessage());
        }
        
        return importClaimDtos;
    }

    /**
     * Convierte una fila de Excel a ImportClaimDto.
     */
    private ImportClaimDto parseRowToDto(Row row, int rowNumber) {
        // Verificar que la fila no esté vacía
        if (isRowEmpty(row)) {
            return null;
        }
        
        try {
            return ImportClaimDto.builder()
                    .date(getCellValueAsDateTime(row.getCell(0)))
                    .amount(getCellValueAsBigDecimal(row.getCell(1)))
                    .identityDocument(getCellValueAsString(row.getCell(2)))
                    .description(getCellValueAsString(row.getCell(3)))
                    .reason(getCellValueAsString(row.getCell(4)))
                    .subReason(getCellValueAsString(row.getCell(5)))
                    .rowNumber(rowNumber)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error parseando fila " + rowNumber + ": " + e.getMessage());
        }
    }

    /**
     * Verifica si una fila está vacía.
     */
    private boolean isRowEmpty(Row row) {
        for (int cellIndex = 0; cellIndex < 6; cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    /**
     * Obtiene el valor de una celda como String.
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * Obtiene el valor de una celda como BigDecimal.
     */
    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING:
                try {
                    return new BigDecimal(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Valor numérico inválido: " + cell.getStringCellValue());
                }
            default:
                throw new RuntimeException("Tipo de celda no soportado para valor numérico");
        }
    }

    /**
     * Obtiene el valor de una celda como LocalDateTime.
     */
    private LocalDateTime getCellValueAsDateTime(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                }
                throw new RuntimeException("Celda numérica no contiene fecha");
            case STRING:
                try {
                    // Aquí puedes agregar parseo de strings a fechas si es necesario
                    throw new RuntimeException("Parseo de fecha desde string no implementado");
                } catch (Exception e) {
                    throw new RuntimeException("Formato de fecha inválido: " + cell.getStringCellValue());
                }
            default:
                throw new RuntimeException("Tipo de celda no soportado para fecha");
        }
    }
}