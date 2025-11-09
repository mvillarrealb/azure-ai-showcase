package org.mavb.azure.ai.demos.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mavb.azure.ai.demos.dto.request.ImportClaimDto;
import org.mavb.azure.ai.demos.exception.InvalidFileException;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utilidad para parsear archivos Excel que contienen datos de reclamos.
 * Maneja toda la lógica de Apache POI separada del servicio de negocio.
 */
@Component
@Slf4j
public class ExcelClaimParser {

    private static final int MAX_FILE_SIZE_MB = 10;
    private static final int MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024;

    /**
     * Parsea un archivo Excel desde bytes y convierte las filas a ImportClaimDto.
     * 
     * @param fileBytes Los bytes del archivo Excel
     * @return Lista de ImportClaimDto parseados desde el Excel
     * @throws InvalidFileException Si hay errores en el formato o tamaño del archivo
     */
    public List<ImportClaimDto> parseExcelFromBytes(byte[] fileBytes) {
        validateFileSize(fileBytes);
        
        List<ImportClaimDto> importClaimDtos = new ArrayList<>();
        
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes); 
             Workbook workbook = new XSSFWorkbook(bis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            log.info("Parseando Excel con {} filas", sheet.getLastRowNum());
            
            // Iterar desde la fila 1 (saltando el header en fila 0)
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
                    // No lanzamos excepción aquí, permitimos que el servicio maneje los errores por fila
                }
            }
            
        } catch (IOException e) {
            throw new InvalidFileException("Error leyendo el archivo Excel: " + e.getMessage());
        }
        
        log.info("Parseo completado. {} filas procesadas", importClaimDtos.size());
        return importClaimDtos;
    }

    /**
     * Convierte una fila de Excel a ImportClaimDto.
     * Formato esperado: Fecha, Monto, Documento de Identidad, Descripción
     */
    private ImportClaimDto parseRowToDto(Row row, int rowNumber) {
        if (isRowEmpty(row)) {
            return null;
        }
        
        try {
            return ImportClaimDto.builder()
                    .date(getCellValueAsDateTime(row.getCell(0)))
                    .amount(getCellValueAsBigDecimal(row.getCell(1)))
                    .identityDocument(getCellValueAsString(row.getCell(2)))
                    .description(getCellValueAsString(row.getCell(3)))
                    .rowNumber(rowNumber)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error parseando fila " + rowNumber + ": " + e.getMessage());
        }
    }

    /**
     * Verifica si una fila está completamente vacía.
     */
    private boolean isRowEmpty(Row row) {
        for (int cellIndex = 0; cellIndex < 4; cellIndex++) { // Solo 4 columnas esperadas
            Cell cell = row.getCell(cellIndex);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
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
            case BLANK:
                return null;
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
                String value = cell.getStringCellValue().trim();
                if (value.isEmpty()) return null;
                try {
                    return new BigDecimal(value);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Valor numérico inválido: " + value);
                }
            case BLANK:
                return null;
            default:
                throw new RuntimeException("Tipo de celda no soportado para valor numérico: " + cell.getCellType());
        }
    }

    /**
     * Obtiene el valor de una celda como LocalDateTime.
     * Soporta fechas ISO en formato STRING (2024-11-01 o 2024-11-01T10:30:00)
     * y fechas numéricas formateadas como fecha.
     */
    private LocalDateTime getCellValueAsDateTime(Cell cell) {
        if (cell == null) return null;
        
        // Manejo de fechas numéricas (Excel nativo)
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        
        // Manejo de fechas en formato STRING (ISO)
        if (cell.getCellType() == CellType.STRING) {
            String cellValue = cell.getStringCellValue().trim();
            if (cellValue.isEmpty()) {
                return null;
            }
            
            try {
                // Verificar si es fecha ISO (YYYY-MM-DD)
                if (cellValue.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    return LocalDateTime.parse(cellValue + "T00:00:00");
                }
                
                // Verificar si es datetime ISO (YYYY-MM-DDTHH:mm:ss o similar)
                if (cellValue.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*")) {
                    // Remover posibles zonas horarias y milisegundos para simplificar
                    String cleanDateTime = cellValue
                            .replaceAll("\\[.*\\]", "")
                            .replaceAll("\\+.*", "")
                            .replaceAll("Z.*", "");
                    
                    if (cleanDateTime.contains(".")) {
                        cleanDateTime = cleanDateTime.substring(0, cleanDateTime.indexOf("."));
                    }
                    return LocalDateTime.parse(cleanDateTime);
                }
                
                // Si no coincide con ningún formato ISO esperado
                throw new RuntimeException("Formato de fecha no soportado. Use formato ISO: YYYY-MM-DD o YYYY-MM-DDTHH:mm:ss");
                
            } catch (Exception e) {
                throw new RuntimeException("Formato de fecha inválido: " + cellValue + ". Error: " + e.getMessage());
            }
        }
        
        if (cell.getCellType() == CellType.BLANK) {
            return null;
        }
        
        throw new RuntimeException("Tipo de celda no soportado para fecha. Debe ser NUMERIC con formato de fecha o STRING con formato ISO");
    }

    /**
     * Valida el tamaño del archivo.
     */
    private void validateFileSize(byte[] fileBytes) {
        if (fileBytes.length > MAX_FILE_SIZE_BYTES) {
            throw new InvalidFileException("El archivo es demasiado grande. Tamaño máximo: " + MAX_FILE_SIZE_MB + "MB");
        }
    }
}