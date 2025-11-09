package org.mavb.azure.ai.controller;

import com.azure.search.documents.SearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.dto.RankUploadDto;
import org.mavb.azure.ai.mapper.RankDocumentMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing rank uploads to Azure AI Search.
 * Provides endpoints for loading rank classifications directly to the search index.
 */
@RestController
@RequestMapping("/ranks")
@RequiredArgsConstructor
@Slf4j
public class RankController {

    @Qualifier("rankSearchClient")
    private final SearchClient rankSearchClient;
    private final RankDocumentMapper rankDocumentMapper;

    /**
     * Uploads a single rank to Azure AI Search.
     * 
     * @param rankDto DTO containing rank information (id, name, description)
     * @return ResponseEntity with upload result
     */
    @PostMapping("/upload")
    public Mono<ResponseEntity<Map<String, Object>>> uploadRank(@Valid @RequestBody RankUploadDto rankDto) {
        log.info("🏆 Uploading rank to Azure AI Search: {} ({})", rankDto.getName(), rankDto.getId());

        // Convert to RankDocument with embeddings and upload
        return rankDocumentMapper.toRankDocument(rankDto)
                .flatMap(rankDocument -> {
                    // Upload to Azure AI Search using blocking scheduler
                    return Mono.fromCallable(() -> rankSearchClient.uploadDocuments(List.of(rankDocument)))
                            .subscribeOn(Schedulers.boundedElastic())
                            .map(result -> {
                                boolean success = result.getResults().stream()
                                        .allMatch(indexResult -> indexResult.isSucceeded());

                                if (success) {
                                    log.info("✅ Rank {} successfully uploaded to Azure AI Search", rankDto.getName());
                                    return ResponseEntity.status(HttpStatus.CREATED)
                                            .<Map<String, Object>>body(Map.of(
                                                    "success", true,
                                                    "message", "Rank uploaded successfully",
                                                    "rankId", rankDto.getId(),
                                                    "rankName", rankDto.getName()
                                            ));
                                } else {
                                    log.error("❌ Failed to upload rank {} to Azure AI Search", rankDto.getName());
                                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                            .<Map<String, Object>>body(Map.of(
                                                    "success", false,
                                                    "message", "Failed to upload rank to search index"
                                            ));
                                }
                            });
                })
                .onErrorResume(e -> {
                    log.error("❌ Error uploading rank {}: {}", rankDto.getName(), e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .<Map<String, Object>>body(Map.of(
                                    "success", false,
                                    "message", "Error uploading rank: " + e.getMessage()
                            )));
                });
    }

    /**
     * Uploads multiple ranks to Azure AI Search in batch.
     *
     * @param ranksData Map containing array of ranks
     * @return ResponseEntity with batch upload result
     */
    @PostMapping("/upload-batch")
    public Mono<ResponseEntity<Map<String, Object>>> uploadRanksBatch(@RequestBody Map<String, List<Map<String, String>>> ranksData) {
        List<Map<String, String>> ranks = ranksData.get("ranks");
        
        if (ranks == null || ranks.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest()
                    .<Map<String, Object>>body(Map.of(
                            "success", false,
                            "message", "No ranks provided"
                    )));
        }

        log.info("🏆 Uploading {} ranks to Azure AI Search in batch", ranks.size());

        return Flux.fromIterable(ranks)
                .map(rankData -> {
                    // Convert Map to DTO for cleaner processing
                    RankUploadDto dto = new RankUploadDto();
                    dto.setId(rankData.get("id"));
                    dto.setName(rankData.get("name"));
                    dto.setDescription(rankData.get("description"));
                    return dto;
                })
                .flatMap(rankDto -> rankDocumentMapper.toRankDocument(rankDto))
                .collectList()
                .flatMap(rankDocuments -> {
                    // Upload all ranks in batch using blocking scheduler
                    return Mono.fromCallable(() -> rankSearchClient.uploadDocuments(rankDocuments))
                            .subscribeOn(Schedulers.boundedElastic())
                            .map(result -> {
                                long successCount = result.getResults().stream()
                                        .mapToLong(indexResult -> indexResult.isSucceeded() ? 1 : 0)
                                        .sum();

                                log.info("✅ Successfully uploaded {}/{} ranks to Azure AI Search", successCount, ranks.size());

                                return ResponseEntity.status(HttpStatus.CREATED)
                                        .<Map<String, Object>>body(Map.of(
                                                "success", true,
                                                "message", "Batch upload completed",
                                                "totalRanks", ranks.size(),
                                                "successfulUploads", successCount,
                                                "failedUploads", ranks.size() - successCount
                                        ));
                            });
                })
                .onErrorResume(e -> {
                    log.error("❌ Error uploading ranks batch: {}", e.getMessage(), e);

                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .<Map<String, Object>>body(Map.of(
                                    "success", false,
                                    "message", "Error processing batch upload: " + e.getMessage(),
                                    "totalRanks", ranks.size(),
                                    "successfulUploads", 0,
                                    "failedUploads", ranks.size()
                            )));
                });
    }
}