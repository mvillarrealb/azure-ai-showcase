package org.mavb.azure.ai.demos.dto.response;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para elementos de línea de factura según esquema InvoiceLineItem del OpenAPI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceLineItemDto {
    
    /**
     * Descripción del producto o servicio
     */
    private String description;
    
    /**
     * Cantidad del producto o servicio
     */
    private BigDecimal quantity;
    
    /**
     * Precio unitario del producto o servicio
     */
    private BigDecimal unitPrice;
    
    /**
     * Precio total de la línea (cantidad × precio unitario)
     */
    private BigDecimal totalPrice;
}