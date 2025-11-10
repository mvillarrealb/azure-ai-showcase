package org.mavb.azure.ai.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for rank filtering parameters.
 * Used with @RequestParam for query parameter handling in rank searches.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankFilterDTO {

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;
}