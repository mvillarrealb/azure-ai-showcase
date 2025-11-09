package org.mavb.azure.ai.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents employment gap analysis result with gap duration and description.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentGapAnalysis {
    
    private long gapInMonths;
    private String description;
}