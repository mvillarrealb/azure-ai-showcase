package org.mavb.azure.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for credit evaluation requests.
 * Contains customer identification and loan requirements for eligibility assessment.
 * Additional customer information (income, debt, etc.) is retrieved from database.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequestDTO {

    @JsonProperty("identityDocument")
    @NotBlank(message = "Identity document is required")
    @Size(min = 8, max = 11, message = "Identity document must be between 8 and 11 characters")
    @Pattern(regexp = "^[0-9]+$", message = "Identity document must contain only numbers")
    private String identityDocument;

    @JsonProperty("requestedAmount")
    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "1.0", inclusive = true, message = "Requested amount must be greater than 0")
    @Digits(integer = 15, fraction = 2, message = "Invalid amount format")
    private BigDecimal requestedAmount;
}