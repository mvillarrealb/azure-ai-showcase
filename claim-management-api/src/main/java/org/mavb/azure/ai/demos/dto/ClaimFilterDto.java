package org.mavb.azure.ai.demos.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO para filtros de búsqueda de reclamos.
 * Se usa para query parameters cuando hay más de 2 parámetros.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimFilterDto {

    @Size(min = 8, max = 12, message = "El documento de identidad debe tener entre 8 y 12 caracteres")
    private String identityDocument;

    @Min(value = 1, message = "La página debe ser mayor a 0")
    private Integer page = 1;

    @Min(value = 1, message = "El límite debe ser al menos 1")
    @Max(value = 100, message = "El límite máximo es 100")
    private Integer limit = 20;

    @Pattern(regexp = "^(open|inProgress|resolved)$", message = "El estado debe ser: open, inProgress o resolved")
    private String status;
}