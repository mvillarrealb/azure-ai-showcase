package org.mavb.azure.ai.demos.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para importación de reclamos desde Excel.
 * Contiene las validaciones para cada fila del archivo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportClaimDto {

    @NotNull(message = "La fecha es requerida")
    private LocalDateTime date;

    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    private BigDecimal amount;

    @NotBlank(message = "El documento de identidad es requerido")
    @Size(min = 8, max = 12, message = "El documento de identidad debe tener entre 8 y 12 caracteres")
    private String identityDocument;

    @NotBlank(message = "La descripción es requerida")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    private String description;

    @NotBlank(message = "El motivo es requerido")
    @Size(min = 3, max = 100, message = "El motivo debe tener entre 3 y 100 caracteres")
    private String reason;

    @NotBlank(message = "El submotivo es requerido")
    @Size(min = 3, max = 100, message = "El submotivo debe tener entre 3 y 100 caracteres")
    private String subReason;

    // Campo temporal para tracking de errores durante la importación
    private Integer rowNumber;
}