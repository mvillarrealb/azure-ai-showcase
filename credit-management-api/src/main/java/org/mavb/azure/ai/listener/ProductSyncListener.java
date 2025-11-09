package org.mavb.azure.ai.listener;

import com.azure.search.documents.SearchClient;
import com.azure.search.documents.models.IndexDocumentsResult;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.entity.CreditProductEntity;
import org.mavb.azure.ai.entity.ProductDocument;
import org.mavb.azure.ai.mapper.ProductDocumentMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * JPA Entity Listener for automatic synchronization of CreditProductEntity to Azure AI Search.
 * Provides real-time search index updates for INSERT operations only.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ProductSyncListener {

    private final ProductDocumentMapper productDocumentMapper;
    @Qualifier("productSearchClient")
    private final SearchClient productSearchClient;

    /**
     * Called after a product is persisted to the database.
     * Automatically indexes the product in Azure AI Search.
     *
     * @param product The newly created product entity
     */
    @PostPersist
    @Async("aiSearchSyncExecutor")
    public void afterInsert(CreditProductEntity product) {
        log.info("🚀 Product inserted, starting AI Search indexing for: {}", product.getId());
        
        try {
            // Convert entity to search document with embeddings
            ProductDocument document = productDocumentMapper.toProductDocument(product);
            
            // Index document in Azure AI Search
            IndexDocumentsResult result = productSearchClient.uploadDocuments(java.util.List.of(document));
            
            log.info("✅ Product {} successfully indexed in AI Search. Results: {}", 
                    product.getId(), result.getResults().size());
            
            // Log individual index results for debugging
            result.getResults().forEach(indexResult -> 
                log.debug("Index result for {}: status={}, statusCode={}", 
                    indexResult.getKey(), indexResult.isSucceeded(), indexResult.getStatusCode())
            );
            
        } catch (Exception e) {
            log.error("❌ Failed to index product {} in AI Search: {}", 
                    product.getId(), e.getMessage(), e);
        }
    }
}