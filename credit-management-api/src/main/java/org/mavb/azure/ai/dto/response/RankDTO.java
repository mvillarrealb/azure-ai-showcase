package org.mavb.azure.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a rank for API responses.
 * Contains complete rank information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;
}