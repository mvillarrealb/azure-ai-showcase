package org.mavb.azure.ai.demos.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.demos.dto.response.InvoiceAnalysisDto;
import org.mavb.azure.ai.demos.dto.response.InvoiceLineItemDto;
import org.mavb.azure.ai.demos.exception.ProcessingException;
import org.mavb.azure.ai.demos.exception.ValidationException;
import org.mavb.azure.ai.demos.service.InvoiceService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de procesamiento de facturas
 * 
 * NOTA: Esta es una implementación de ejemplo/mock.
 * En un entorno real, aquí se integraría con servicios de OCR como:
 * - Azure Computer Vision
 * - Google Cloud Vision API
 * - AWS Textract
 * - Tesseract OCR
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_CONTENT_TYPES = {"application/pdf"};
    
    @Override
    public InvoiceAnalysisDto scanInvoice(MultipartFile file) {
        log.debug("Procesando factura PDF: {}", file.getOriginalFilename());
        
        // Validaciones del archivo
        validateFile(file);
        
        try {
            // TODO: Aquí iría la integración real con servicio de OCR
            // Por ahora, retornamos datos de ejemplo
            InvoiceAnalysisDto mockResult = createMockInvoiceAnalysis();
            
            log.info("Factura procesada exitosamente: {}", mockResult.getInvoiceNumber());
            
            return mockResult;
            
        } catch (Exception e) {
            log.error("Error procesando factura: {}", e.getMessage(), e);
            throw new ProcessingException("Error al procesar la factura: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valida el archivo de factura
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("El archivo es requerido");
        }
        
        // Validar tamaño
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException("El archivo excede el tamaño máximo permitido de 10MB");
        }
        
        // Validar tipo de contenido
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new ValidationException("Solo se permiten archivos PDF");
        }
        
        // Validar extensión del archivo
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            throw new ValidationException("El archivo debe tener extensión .pdf");
        }
    }
    
    /**
     * Verifica si el tipo de contenido está permitido
     */
    private boolean isAllowedContentType(String contentType) {
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (allowedType.equals(contentType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Crea un análisis de factura de ejemplo
     * TODO: Reemplazar con integración real de OCR
     */
    private InvoiceAnalysisDto createMockInvoiceAnalysis() {
        List<InvoiceLineItemDto> lineItems = List.of(
            InvoiceLineItemDto.builder()
                .description("Pan integral")
                .quantity(new BigDecimal("2"))
                .unitPrice(new BigDecimal("3.50"))
                .totalPrice(new BigDecimal("7.00"))
                .build(),
            InvoiceLineItemDto.builder()
                .description("Leche descremada 1L")
                .quantity(new BigDecimal("1"))
                .unitPrice(new BigDecimal("4.25"))
                .totalPrice(new BigDecimal("4.25"))
                .build(),
            InvoiceLineItemDto.builder()
                .description("Manzanas rojas (kg)")
                .quantity(new BigDecimal("1.5"))
                .unitPrice(new BigDecimal("6.80"))
                .totalPrice(new BigDecimal("10.20"))
                .build()
        );
        
        return InvoiceAnalysisDto.builder()
            .invoiceNumber("INV-2024-001234")
            .date(LocalDateTime.of(2024, 11, 5, 14, 30))
            .totalAmount(new BigDecimal("21.45"))
            .vendor("Supermercado La Esperanza")
            .lineItems(lineItems)
            .build();
    }
}