package org.mavb.azure.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for product filtering parameters.
 * Used with @RequestParam for query parameter handling in product searches.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterDTO {

    private String category;

    @Pattern(regexp = "^(S/|USD)$", message = "Currency must be either 'S/' or 'USD'")
    private String currency;

    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum amount must be greater than or equal to 0")
    @Digits(integer = 15, fraction = 2, message = "Invalid minimum amount format")
    private BigDecimal minAmount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Maximum amount must be greater than or equal to 0")
    @Digits(integer = 15, fraction = 2, message = "Invalid maximum amount format")
    private BigDecimal maxAmount;
}