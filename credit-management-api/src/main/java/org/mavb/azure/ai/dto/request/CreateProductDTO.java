package org.mavb.azure.ai.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para la creación de productos crediticios.
 * Contiene toda la información necesaria para crear un nuevo producto
 * que será sincronizado automáticamente con Azure AI Search.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDTO {

    @NotBlank(message = "El ID del producto es obligatorio")
    @Size(max = 20, message = "El ID no puede exceder 20 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "El ID solo puede contener letras, números, guiones y guiones bajos")
    private String id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @NotBlank(message = "La descripción del producto es obligatoria")
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String category;

    @NotBlank(message = "La subcategoría es obligatoria")
    @Size(max = 100, message = "La subcategoría no puede exceder 100 caracteres")
    private String subcategory;

    @NotNull(message = "El monto mínimo es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto mínimo debe ser mayor a 0")
    @Digits(integer = 13, fraction = 2, message = "El monto mínimo debe tener máximo 13 enteros y 2 decimales")
    private BigDecimal minimumAmount;

    @NotNull(message = "El monto máximo es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto máximo debe ser mayor a 0")
    @Digits(integer = 13, fraction = 2, message = "El monto máximo debe tener máximo 13 enteros y 2 decimales")
    private BigDecimal maximumAmount;

    @NotBlank(message = "La moneda es obligatoria")
    @Pattern(regexp = "^(S/|USD)$", message = "La moneda debe ser 'S/' o 'USD'")
    private String currency;

    @NotBlank(message = "El término es obligatorio")
    @Size(max = 50, message = "El término no puede exceder 50 caracteres")
    private String term;

    @NotNull(message = "La tasa mínima es obligatoria")
    @DecimalMin(value = "0.0", message = "La tasa mínima debe ser mayor o igual a 0")
    @DecimalMax(value = "100.0", message = "La tasa mínima no puede ser mayor a 100%")
    @Digits(integer = 3, fraction = 2, message = "La tasa mínima debe tener máximo 3 enteros y 2 decimales")
    private BigDecimal minimumRate;

    @NotNull(message = "La tasa máxima es obligatoria")
    @DecimalMin(value = "0.0", message = "La tasa máxima debe ser mayor o igual a 0")
    @DecimalMax(value = "100.0", message = "La tasa máxima no puede ser mayor a 100%")
    @Digits(integer = 3, fraction = 2, message = "La tasa máxima debe tener máximo 3 enteros y 2 decimales")
    private BigDecimal maximumRate;

    @NotNull(message = "Los requisitos son obligatorios")
    @Size(min = 1, message = "Debe incluir al menos un requisito")
    private List<@NotBlank(message = "Los requisitos no pueden estar vacíos") String> requirements;

    @NotNull(message = "Las características son obligatorias")
    @Size(min = 1, message = "Debe incluir al menos una característica")
    private List<@NotBlank(message = "Las características no pueden estar vacías") String> features;

    @NotNull(message = "Los beneficios son obligatorios")
    @Size(min = 1, message = "Debe incluir al menos un beneficio")
    private List<@NotBlank(message = "Los beneficios no pueden estar vacíos") String> benefits;

    @Builder.Default
    private Boolean active = true;

    /**
     * Valida que el monto máximo sea mayor al mínimo.
     */
    @AssertTrue(message = "El monto máximo debe ser mayor al monto mínimo")
    public boolean isMaximumAmountValid() {
        return maximumAmount != null && minimumAmount != null && 
               maximumAmount.compareTo(minimumAmount) > 0;
    }

    /**
     * Valida que la tasa máxima sea mayor o igual a la mínima.
     */
    @AssertTrue(message = "La tasa máxima debe ser mayor o igual a la tasa mínima")
    public boolean isMaximumRateValid() {
        return maximumRate != null && minimumRate != null && 
               maximumRate.compareTo(minimumRate) >= 0;
    }
}