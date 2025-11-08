package org.mavb.azure.ai.demos.service;

import org.mavb.azure.ai.demos.dto.response.InvoiceAnalysisDto;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

/**
 * Interfaz del servicio para procesamiento de facturas
 */
public interface InvoiceService {
    
    /**
     * Procesa un archivo PDF de factura usando OCR
     * @param file Archivo PDF a procesar
     * @return Análisis de la factura con datos extraídos
     */
    InvoiceAnalysisDto scanInvoice(MultipartFile file);
    
    /**
     * Procesa un archivo PDF de factura usando OCR (versión WebFlux)
     * @param filePart Archivo PDF a procesar como FilePart
     * @return Mono con el análisis de la factura con datos extraídos
     */
    Mono<InvoiceAnalysisDto> scanInvoiceFromFilePart(FilePart filePart);
}