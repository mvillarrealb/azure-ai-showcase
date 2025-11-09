package org.mavb.azure.ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for batch creation response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RanksBatchResponseDTO {

    private Boolean success;
    private String message;
    private Integer totalRanks;
    private Integer createdRanks;
    private Integer failedRanks;
    private List<String> createdRankIds;
}