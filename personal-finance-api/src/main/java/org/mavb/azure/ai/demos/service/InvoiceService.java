package org.mavb.azure.ai.demos.service;

import org.mavb.azure.ai.demos.dto.response.InvoiceAnalysisDto;
import org.springframework.web.multipart.MultipartFile;

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
}