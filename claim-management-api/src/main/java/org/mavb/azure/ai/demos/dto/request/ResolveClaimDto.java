package org.mavb.azure.ai.demos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO para resolver un reclamo.
 * Contiene los comentarios de resolución requeridos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResolveClaimDto {

    @NotBlank(message = "Los comentarios de resolución son requeridos")
    @Size(min = 10, max = 1000, message = "Los comentarios deben tener entre 10 y 1000 caracteres")
    private String comments;
}