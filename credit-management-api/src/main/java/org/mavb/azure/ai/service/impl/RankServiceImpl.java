package org.mavb.azure.ai.service.impl;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.Embeddings;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.models.*;
import com.azure.search.documents.util.SearchPagedIterable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.config.AzureProperties;
import org.mavb.azure.ai.dto.request.CreateRankDTO;
import org.mavb.azure.ai.dto.request.CreateRanksBatchDTO;
import org.mavb.azure.ai.dto.request.RankFilterDTO;
import org.mavb.azure.ai.dto.response.RankDTO;
import org.mavb.azure.ai.dto.response.RankListResponseDTO;
import org.mavb.azure.ai.dto.response.RanksBatchResponseDTO;
import org.mavb.azure.ai.entity.RankDocument;
import org.mavb.azure.ai.entity.RankEntity;
import org.mavb.azure.ai.exception.RankAlreadyExistsException;
import org.mavb.azure.ai.exception.RankNotFoundException;
import org.mavb.azure.ai.mapper.RankMapper;
import org.mavb.azure.ai.repository.RankRepository;
import org.mavb.azure.ai.repository.RankSpecifications;
import org.mavb.azure.ai.service.RankService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of RankService interface.
 * Provides business logic for rank operations including filtering, searching and AI-powered rank resolution.
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class RankServiceImpl implements RankService {

    private final RankRepository rankRepository;
    private final RankMapper rankMapper;
    private final OpenAIClient openAI;
    private final SearchClient rankSearch;
    private final AzureProperties azure;

    public RankServiceImpl(RankRepository rankRepository, RankMapper rankMapper, 
                          OpenAIClient openAI, @Qualifier("rankSearchClient") SearchClient rankSearch, 
                          AzureProperties azure) {
        this.rankRepository = rankRepository;
        this.rankMapper = rankMapper;
        this.openAI = openAI;
        this.rankSearch = rankSearch;
        this.azure = azure;
    }

    @Override
    public RankListResponseDTO getRanks(RankFilterDTO filter, Pageable pageable) {
        log.info("Retrieving ranks with filter: {} and pagination: {}", filter, pageable);
        
        // Construir la especificación usando el patrón estándar del proyecto
        Specification<RankEntity> spec = RankSpecifications.isActive();
        
        // Agregar filtro por nombre si está presente
        if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
            spec = spec.and(RankSpecifications.hasNameContaining(filter.getName()));
        }
        
        // Ejecutar consulta usando JpaSpecificationExecutor
        Page<RankEntity> ranksPage = rankRepository.findAll(spec, pageable);

        List<RankDTO> rankDTOs = rankMapper.toDtoList(ranksPage.getContent());

        return RankListResponseDTO.builder()
                .data(rankDTOs)
                .total((int) ranksPage.getTotalElements())
                .totalPages(ranksPage.getTotalPages())
                .currentPage(pageable.getPageNumber())
                .build();
    }

    @Override
    @Transactional
    public RankDTO createRank(CreateRankDTO createRankDTO) {
        log.info("Creating new rank with ID: {}", createRankDTO.getId());

        // Check if rank ID already exists
        if (rankRepository.existsById(createRankDTO.getId())) {
            throw new RankAlreadyExistsException("Rank with ID '" + createRankDTO.getId() + "' already exists");
        }

        RankEntity rank = RankEntity.builder()
                .id(createRankDTO.getId())
                .name(createRankDTO.getName())
                .description(createRankDTO.getDescription())
                .active(createRankDTO.getActive())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        RankEntity savedRank = rankRepository.save(rank);

        RankDTO rankDTO = rankMapper.toDto(savedRank);
        
        log.info("Successfully created rank: {} with ID: {}", savedRank.getName(), savedRank.getId());
        return rankDTO;
    }

    @Override
    @Transactional
    public RanksBatchResponseDTO createRanksBatch(CreateRanksBatchDTO createRanksBatchDTO) {
        int totalRanks = createRanksBatchDTO.getRanks().size();
        log.info("Creating {} ranks in batch using saveAll approach", totalRanks);

        try {
            // Convert DTOs to entities
            List<RankEntity> rankEntities = createRanksBatchDTO.getRanks().stream()
                    .map(createRankDTO -> RankEntity.builder()
                            .id(createRankDTO.getId())
                            .name(createRankDTO.getName())
                            .description(createRankDTO.getDescription())
                            .active(createRankDTO.getActive())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build())
                    .toList();

            // Save all entities in a single transaction
            List<RankEntity> savedRanks = rankRepository.saveAll(rankEntities);
            
            // Extract created rank IDs
            List<String> createdRankIds = savedRanks.stream()
                    .map(RankEntity::getId)
                    .toList();

            int createdCount = savedRanks.size();
            int failedCount = totalRanks - createdCount;
            boolean success = createdCount > 0;

            String message = String.format("Batch processing completed: %d/%d ranks created successfully", 
                    createdCount, totalRanks);

            log.info("Batch creation completed successfully: {}/{} ranks created", createdCount, totalRanks);

            return RanksBatchResponseDTO.builder()
                    .success(success)
                    .message(message)
                    .totalRanks(totalRanks)
                    .createdRanks(createdCount)
                    .failedRanks(failedCount)
                    .createdRankIds(createdRankIds)
                    .build();

        } catch (Exception e) {
            log.error("Failed to create ranks in batch: {}", e.getMessage(), e);
            
            // Return failure response
            return RanksBatchResponseDTO.builder()
                    .success(false)
                    .message("Batch creation failed: " + e.getMessage())
                    .totalRanks(totalRanks)
                    .createdRanks(0)
                    .failedRanks(totalRanks)
                    .createdRankIds(new ArrayList<>())
                    .build();
        }
    }

    @Override
    public RankDocument resolveRank(String clientDescription) {
        log.info("Resolving rank for client description using AI semantic search");
        
        try {
            // Create embeddings options with proper configuration
            EmbeddingsOptions options = new EmbeddingsOptions(List.of(clientDescription));
            options.setUser("rank-resolution-system");
            options.setInputType("text");
            
            Embeddings emb = openAI.getEmbeddings(
                    azure.getOpenai().getEmbeddingModel(),
                    options
            );
            List<Float> vector = emb.getData().get(0).getEmbedding().stream().map(Double::floatValue).toList();

            VectorizedQuery vectorQuery = new VectorizedQuery(vector)
                    .setKNearestNeighborsCount(1)
                    .setFields("embedding");
            
            SearchOptions opts = new SearchOptions()
                    .setVectorSearchOptions(new VectorSearchOptions().setQueries(vectorQuery))
                    .setTop(1);

            SearchPagedIterable results = rankSearch.search(null, opts, null);
            RankDocument resolvedRank = results.stream()
                    .findFirst()
                    .map(r -> r.getDocument(RankDocument.class))
                    .orElse(null);

            if (resolvedRank != null) {
                log.info("Successfully resolved rank: {} for client description", resolvedRank.getId());
            } else {
                log.warn("No rank resolved for the provided client description");
            }

            return resolvedRank;
        } catch (Exception e) {
            log.error("Error resolving rank for client description: {}", e.getMessage(), e);
            return null;
        }
    }
}