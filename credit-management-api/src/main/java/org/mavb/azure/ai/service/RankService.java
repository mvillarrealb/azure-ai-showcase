package org.mavb.azure.ai.service;

import org.mavb.azure.ai.dto.request.CreateRankDTO;
import org.mavb.azure.ai.dto.request.CreateRanksBatchDTO;
import org.mavb.azure.ai.dto.request.RankFilterDTO;
import org.mavb.azure.ai.dto.response.RankDTO;
import org.mavb.azure.ai.dto.response.RankListResponseDTO;
import org.mavb.azure.ai.dto.response.RanksBatchResponseDTO;
import org.mavb.azure.ai.entity.RankDocument;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for rank operations.
 * Defines business logic methods for rank management and searching.
 */
public interface RankService {

    /**
     * Get paginated list of ranks with optional filtering.
     * 
     * @param filter Filter criteria for ranks
     * @param pageable Pagination information
     * @return Paginated list of ranks
     */
    RankListResponseDTO getRanks(RankFilterDTO filter, Pageable pageable);

    /**
     * Create a new rank.
     * 
     * @param createRankDTO Rank creation data
     * @return Created rank details
     */
    RankDTO createRank(CreateRankDTO createRankDTO);

    /**
     * Create multiple ranks in batch.
     * 
     * @param createRanksBatchDTO Batch creation data with list of ranks
     * @return Batch creation response with statistics
     */
    RanksBatchResponseDTO createRanksBatch(CreateRanksBatchDTO createRanksBatchDTO);

    /**
     * Resolve the most appropriate rank for a client description using AI semantic search.
     * This method uses Azure AI Search with vector search to find the best matching rank.
     * 
     * @param clientDescription Description of the client's profile
     * @return Best matching rank document or null if none found
     */
    RankDocument resolveRank(String clientDescription);
}