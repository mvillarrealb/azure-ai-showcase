package org.mavb.azure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for credit evaluation requests.
 * Contains customer information and loan requirements for eligibility assessment.
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

    @JsonProperty("requestedCurrency")
    @NotBlank(message = "Requested currency is required")
    @Pattern(regexp = "^(S/|USD)$", message = "Currency must be either 'S/' or 'USD'")
    private String requestedCurrency;

    @JsonProperty("category")
    private String category;

    @JsonProperty("additionalInfo")
    @Valid
    private AdditionalInfoDTO additionalInfo;

    /**
     * DTO for additional customer information used in evaluation.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalInfoDTO {

        @JsonProperty("monthlyIncome")
        @DecimalMin(value = "0.0", inclusive = true, message = "Monthly income must be greater than or equal to 0")
        @Digits(integer = 15, fraction = 2, message = "Invalid income format")
        private BigDecimal monthlyIncome;

        @JsonProperty("currentDebt")
        @DecimalMin(value = "0.0", inclusive = true, message = "Current debt must be greater than or equal to 0")
        @Digits(integer = 15, fraction = 2, message = "Invalid debt format")
        private BigDecimal currentDebt;

        @JsonProperty("creditScore")
        @Min(value = 300, message = "Credit score must be at least 300")
        @Max(value = 850, message = "Credit score must be at most 850")
        private Integer creditScore;

        @JsonProperty("employmentType")
        @Pattern(regexp = "^(Dependiente|Independiente|Empresario)$", 
                message = "Employment type must be one of: Dependiente, Independiente, Empresario")
        private String employmentType;
    }
}