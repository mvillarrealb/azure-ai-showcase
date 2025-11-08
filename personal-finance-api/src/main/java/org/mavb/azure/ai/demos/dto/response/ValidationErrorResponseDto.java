package org.mavb.azure.ai.demos.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para errores de validación con detalles por campo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationErrorResponseDto {
    
    /**
     * Título breve del error
     */
    private String error;
    
    /**
     * Descripción detallada del error
     */
    private String message;
    
    /**
     * Código único del error
     */
    private String code;
    
    /**
     * Mapa de errores por campo
     */
    private Map<String, String> fieldErrors;
    
    /**
     * Timestamp del error
     */
    private LocalDateTime timestamp;
}