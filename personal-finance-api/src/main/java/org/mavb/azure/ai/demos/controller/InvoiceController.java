package org.mavb.azure.ai.demos.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.demos.dto.response.InvoiceAnalysisDto;
import org.mavb.azure.ai.demos.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

/**
 * Controlador para operaciones de facturas según tag "Facturas" del OpenAPI
 */
@RestController
@RequestMapping("/invoices")
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
    @PostMapping(value = "/scan", consumes = "multipart/form-data")
    public Mono<ResponseEntity<InvoiceAnalysisDto>> scanInvoice(
            @RequestPart("file") Mono<FilePart> fileMono) {
        
        return fileMono.flatMap(filePart -> {
            log.debug("POST /invoices/scan - Procesando archivo: {}", filePart.filename());
            
            log.info("Archivo recibido: {} - Headers: {}", 
                    filePart.filename(), filePart.headers());
            
            // Usar el método reactivo del servicio
            return invoiceService.scanInvoiceFromFilePart(filePart)
                    .map(analysis -> {
                        log.info("Factura procesada exitosamente: {}", analysis.getInvoiceNumber());
                        return ResponseEntity.status(HttpStatus.CREATED).body(analysis);
                    });
        });
    }
}