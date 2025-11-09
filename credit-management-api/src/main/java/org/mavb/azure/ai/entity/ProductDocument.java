package org.mavb.azure.ai.entity;

import com.azure.search.documents.indexes.SearchableField;
import com.azure.search.documents.indexes.SimpleField;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {
    @SimpleField(isKey = true)
    private String id;

    @SearchableField
    private String name;

    @SearchableField
    private String description;

    @SimpleField(isFilterable = true)
    private String category;

    @SimpleField(isFilterable = true)
    private String subcategory;

    @SimpleField(isFilterable = true)
    private BigDecimal minimumAmount;

    @SimpleField(isFilterable = true)
    private BigDecimal maximumAmount;

    @SimpleField(isFilterable = true)
    private String currency;

    @SimpleField(isFilterable = true)
    private String term;

    @SimpleField(isFilterable = true)
    private BigDecimal minimumRate;

    @SimpleField(isFilterable = true)
    private BigDecimal maximumRate;

    @SearchableField
    private List<String> requirements;

    @SearchableField
    private List<String> features;

    @SearchableField
    private List<String> benefits;

    @SimpleField(isFilterable = true)
    private Boolean active;

    @SearchableField
    private List<String> allowedRanks;

    @SearchableField(vectorSearchDimensions = 1536, vectorSearchConfiguration = "vector-config")
    private List<Float> embedding;
}