package org.mavb.azure.ai.demos.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.demos.dto.response.InvoiceAnalysisDto;
import org.mavb.azure.ai.demos.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador para operaciones de facturas según tag "Facturas" del OpenAPI
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {
    
    private final InvoiceService invoiceService;
    
    /**
     * Escanear y procesar factura PDF
     * Endpoint: POST /invoices/scan
     * 
     * Permite a los usuarios subir un archivo PDF de una factura para su análisis 
     * automático y extracción de datos mediante OCR e inteligencia artificial.
     */
    @PostMapping("/invoices/scan")
    public ResponseEntity<InvoiceAnalysisDto> scanInvoice(
            @RequestParam("file") MultipartFile file) {
        
        log.debug("POST /api/v1/invoices/scan - Procesando archivo: {}", file.getOriginalFilename());
        
        InvoiceAnalysisDto analysis = invoiceService.scanInvoice(file);
        
        log.info("Factura procesada exitosamente: {}", analysis.getInvoiceNumber());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(analysis);
    }
}