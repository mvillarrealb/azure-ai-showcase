package org.mavb.azure.ai.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO para la creación de rangos.
 * Contiene toda la información necesaria para crear un nuevo rango
 * que será sincronizado automáticamente con Azure AI Search.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRankDTO {

    @NotBlank(message = "El ID del rango es obligatorio")
    @Size(max = 20, message = "El ID no puede exceder 20 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "El ID solo puede contener letras, números, guiones y guiones bajos")
    private String id;

    @NotBlank(message = "El nombre del rango es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @NotBlank(message = "La descripción del rango es obligatoria")
    @Size(max = 5000, message = "La descripción no puede exceder 5000 caracteres")
    private String description;

    @Builder.Default
    private Boolean active = true;
}