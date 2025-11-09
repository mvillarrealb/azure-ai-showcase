package org.mavb.azure.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for rank upload operations.
 */
@Data
public class RankUploadDto {
    
    @NotBlank(message = "Rank ID is mandatory")
    private String id;
    
    @NotBlank(message = "Rank name is mandatory")
    private String name;
    
    private String description;
}