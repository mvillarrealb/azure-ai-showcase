package org.mavb.azure.ai.demos.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO que representa un reclamo en las respuestas de la API.
 * Contiene toda la información del reclamo incluyendo campos generados automáticamente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimDto {

    private String id;
    private LocalDateTime date;
    private BigDecimal amount;
    private String identityDocument;
    private String description;
    private String reason;
    private String subReason;
    private String status;
    private String comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}