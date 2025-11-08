package org.mavb.azure.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO representing a credit product for API responses.
 * Contains complete product information including features and benefits.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("category")
    private String category;

    @JsonProperty("subcategory")
    private String subcategory;

    @JsonProperty("minimumAmount")
    private BigDecimal minimumAmount;

    @JsonProperty("maximumAmount")
    private BigDecimal maximumAmount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("term")
    private String term;

    @JsonProperty("minimumRate")
    private BigDecimal minimumRate;

    @JsonProperty("maximumRate")
    private BigDecimal maximumRate;

    @JsonProperty("requirements")
    private List<String> requirements;

    @JsonProperty("features")
    private List<String> features;

    @JsonProperty("benefits")
    private List<String> benefits;
}