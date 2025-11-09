package org.mavb.azure.ai.controller;

import com.azure.search.documents.SearchClient;
import com.azure.search.documents.models.IndexDocumentsResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.entity.RankDocument;
import org.mavb.azure.ai.mapper.RankDocumentMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * @param rankData Map containing rank information (id, name, description)
     * @return ResponseEntity with upload result
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadRank(@RequestBody Map<String, String> rankData) {
        try {
            String id = rankData.get("id");
            String name = rankData.get("name");
            String description = rankData.get("description");

            // Validation
            if (id == null || name == null || description == null) {
                log.warn("Invalid rank data received: missing required fields");
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "Missing required fields: id, name, description"
                        ));
            }

            log.info("🏆 Uploading rank to Azure AI Search: {} ({})", name, id);

            // Convert to RankDocument with embeddings
            RankDocument rankDocument = rankDocumentMapper.toRankDocument(id, name, description);

            // Upload to Azure AI Search
            IndexDocumentsResult result = rankSearchClient.uploadDocuments(List.of(rankDocument));

            // Check results
            boolean success = result.getResults().stream()
                    .allMatch(indexResult -> indexResult.isSucceeded());

            if (success) {
                log.info("✅ Rank {} successfully uploaded to Azure AI Search", name);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of(
                                "success", true,
                                "message", "Rank uploaded successfully",
                                "rankId", id,
                                "rankName", name
                        ));
            } else {
                log.error("❌ Failed to upload rank {} to Azure AI Search", name);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                                "success", false,
                                "message", "Failed to upload rank to Azure AI Search"
                        ));
            }

        } catch (Exception e) {
            log.error("❌ Error uploading rank: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error uploading rank: " + e.getMessage()
                    ));
        }
    }

    /**
     * Uploads multiple ranks to Azure AI Search in batch.
     *
     * @param ranksData Map containing array of ranks
     * @return ResponseEntity with batch upload result
     */
    @PostMapping("/upload-batch")
    public ResponseEntity<Map<String, Object>> uploadRanksBatch(@RequestBody Map<String, List<Map<String, String>>> ranksData) {
        try {
            List<Map<String, String>> ranks = ranksData.get("ranks");
            
            if (ranks == null || ranks.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "No ranks provided"
                        ));
            }

            log.info("🏆 Uploading {} ranks to Azure AI Search in batch", ranks.size());

            List<RankDocument> rankDocuments = ranks.stream()
                    .map(rankData -> {
                        String id = rankData.get("id");
                        String name = rankData.get("name");
                        String description = rankData.get("description");
                        return rankDocumentMapper.toRankDocument(id, name, description);
                    })
                    .toList();

            // Upload all ranks in batch
            IndexDocumentsResult result = rankSearchClient.uploadDocuments(rankDocuments);

            // Check results
            long successCount = result.getResults().stream()
                    .mapToLong(indexResult -> indexResult.isSucceeded() ? 1 : 0)
                    .sum();

            log.info("✅ Successfully uploaded {}/{} ranks to Azure AI Search", successCount, ranks.size());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "success", true,
                            "message", "Batch upload completed",
                            "totalRanks", ranks.size(),
                            "successfulUploads", successCount,
                            "failedUploads", ranks.size() - successCount
                    ));

        } catch (Exception e) {
            log.error("❌ Error uploading ranks batch: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error uploading ranks batch: " + e.getMessage()
                    ));
        }
    }
}