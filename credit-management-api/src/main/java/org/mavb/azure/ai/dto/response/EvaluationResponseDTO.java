package org.mavb.azure.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for credit evaluation responses.
 * Contains customer profile assessment and eligible products with recommendations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponseDTO {

    @JsonProperty("clientProfile")
    private ClientProfileDTO clientProfile;

    @JsonProperty("eligibleProducts")
    private List<EligibleProductDTO> eligibleProducts;

    @JsonProperty("summary")
    private EvaluationSummaryDTO summary;

    /**
     * DTO for customer profile information in evaluation response.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientProfileDTO {

        @JsonProperty("identityDocument")
        private String identityDocument;

        @JsonProperty("creditScore")
        private Integer creditScore;

        @JsonProperty("riskLevel")
        private String riskLevel;

        @JsonProperty("approvedAmount")
        private BigDecimal approvedAmount;

        @JsonProperty("recommendedTerm")
        private String recommendedTerm;

        @JsonProperty("semanticRank")
        private String semanticRank;

        @JsonProperty("semanticConfidence")
        private Double semanticConfidence;

        @JsonProperty("semanticDescription")
        private String semanticDescription;
    }

    /**
     * DTO for eligible product information in evaluation response.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EligibleProductDTO {

        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("approvedAmount")
        private BigDecimal approvedAmount;

        @JsonProperty("approvedRate")
        private BigDecimal approvedRate;

        @JsonProperty("eligibilityScore")
        private Integer eligibilityScore;

        @JsonProperty("recommendation")
        private String recommendation;

        @JsonProperty("conditions")
        private List<String> conditions;
    }

    /**
     * DTO for evaluation summary information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvaluationSummaryDTO {

        @JsonProperty("totalEligibleProducts")
        private Integer totalEligibleProducts;

        @JsonProperty("bestOption")
        private BestOptionDTO bestOption;

        @JsonProperty("evaluationDate")
        private LocalDateTime evaluationDate;
    }

    /**
     * DTO for best product recommendation.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BestOptionDTO {

        @JsonProperty("productId")
        private String productId;

        @JsonProperty("reason")
        private String reason;
    }
}