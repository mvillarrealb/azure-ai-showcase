package org.mavb.azure.ai.demos.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de error según esquema Error del OpenAPI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDto {
    
    /**
     * Título breve del error
     */
    private String error;
    
    /**
     * Descripción detallada del error
     */
    private String message;
    
    /**
     * Código único del error para facilitar el manejo programático
     */
    private String code;
    
    /**
     * Timestamp del error (no está en OpenAPI, pero es útil para logs)
     */
    private LocalDateTime timestamp;
}