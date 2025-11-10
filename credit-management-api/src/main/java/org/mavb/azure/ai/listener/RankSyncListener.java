package org.mavb.azure.ai.listener;

import com.azure.search.documents.SearchClient;
import com.azure.search.documents.models.IndexDocumentsResult;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.entity.RankDocument;
import org.mavb.azure.ai.entity.RankEntity;
import org.mavb.azure.ai.mapper.RankDocumentMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * JPA Entity Listener for automatic synchronization of RankEntity to Azure AI Search.
 * Provides real-time search index updates for INSERT operations only.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RankSyncListener {

    private final RankDocumentMapper rankDocumentMapper;
    @Qualifier("rankSearchClient")
    private final SearchClient rankSearchClient;

    /**
     * Called after a rank is persisted to the database.
     * Automatically indexes the rank in Azure AI Search.
     *
     * @param rank The newly created rank entity
     */
    @PostPersist
    @Async("aiSearchSyncExecutor")
    public void afterInsert(RankEntity rank) {
        log.info("🚀 Rank inserted, starting AI Search indexing for: {}", rank.getId());
        
        try {
            // Convert entity to search document with embeddings (synchronous version for listeners)
            RankDocument document = rankDocumentMapper.toRankDocumentSync(rank);
            
            // Index document in Azure AI Search
            IndexDocumentsResult result = rankSearchClient.uploadDocuments(java.util.List.of(document));
            
            log.info("✅ Rank {} successfully indexed in AI Search. Results: {}", 
                    rank.getId(), result.getResults().size());
            
            // Log individual index results for debugging
            result.getResults().forEach(indexResult -> 
                log.debug("Index result for {}: status={}, statusCode={}", 
                    indexResult.getKey(), indexResult.isSucceeded(), indexResult.getStatusCode())
            );
            
        } catch (Exception e) {
            log.error("❌ Failed to index rank {} in AI Search: {}", 
                    rank.getId(), e.getMessage(), e);
        }
    }
}