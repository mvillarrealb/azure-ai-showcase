package org.mavb.azure.ai.demos.dto.request;

import lombok.Data;

@Data
public class ClaimImportReason {
    private Integer rowNumber;
    private String mainCategory;
    private String subCategory;
}
