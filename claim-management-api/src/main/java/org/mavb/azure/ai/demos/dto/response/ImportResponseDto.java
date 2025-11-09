package org.mavb.azure.ai.demos.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para respuestas de importación de reclamos desde Excel.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportResponseDto {

    private String message;
    private Integer totalProcessed;
    private Integer successful;
    private Integer failed;
    private List<ImportedClaimDto> claimsCreated;
    private List<ImportErrorDto> errors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImportedClaimDto {
        private String id;
        private String identityDocument;
        private BigDecimal amount;
        private String reason;
        private String subReason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImportErrorDto {
        private Integer row;
        private String error;
    }
}