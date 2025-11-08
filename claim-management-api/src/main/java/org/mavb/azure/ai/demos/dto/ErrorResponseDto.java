package org.mavb.azure.ai.demos.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuestas de error estandarizadas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDto {

    private String error;
    private String message;
    private List<FieldErrorDto> details;
    private LocalDateTime timestamp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldErrorDto {
        private String field;
        private String message;
    }
}