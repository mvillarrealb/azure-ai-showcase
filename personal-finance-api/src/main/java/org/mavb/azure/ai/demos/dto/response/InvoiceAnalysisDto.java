package org.mavb.azure.ai.demos.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para análisis de facturas según esquema InvoiceAnalysis del OpenAPI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceAnalysisDto {
    
    /**
     * Número de factura extraído del documento
     */
    private String invoiceNumber;
    
    /**
     * Fecha de la factura en formato ISO 8601
     */
    private LocalDateTime date;
    
    /**
     * Monto total de la factura
     */
    private BigDecimal totalAmount;
    
    /**
     * Nombre del vendedor o proveedor
     */
    private String vendor;
    
    /**
     * Lista detallada de productos o servicios en la factura
     */
    private List<InvoiceLineItemDto> lineItems;
}