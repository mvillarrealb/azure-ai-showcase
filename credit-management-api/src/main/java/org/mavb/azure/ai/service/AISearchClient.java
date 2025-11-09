package org.mavb.azure.ai.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.Embeddings;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.models.*;
import com.azure.search.documents.util.SearchPagedIterable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.config.AzureProperties;
import org.mavb.azure.ai.entity.ProductDocument;
import org.mavb.azure.ai.entity.RankDocument;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Unified AI Search client for semantic searches across ranks and products.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AISearchClient {

    private final OpenAIClient openAIClient;
    @Qualifier("rankSearchClient")
    private final SearchClient rankSearchClient;
    @Qualifier("productSearchClient")
    private final SearchClient productSearchClient;
    private final AzureProperties azureProperties;

    /**
     * Resolves the most appropriate rank using semantic search.
     * 
     * @param clientSemanticDescription Description generated from customer data
     * @return Best matching rank document or null if none found
     */
    public RankDocument resolveRank(String clientSemanticDescription) {
        log.info("Resolving rank using semantic description: {}", clientSemanticDescription);
        
        try {
            // Generate embeddings for client description
            List<Float> clientEmbedding = generateEmbeddings(clientSemanticDescription);
            
            // Create vector query
            VectorizedQuery vectorQuery = new VectorizedQuery(clientEmbedding)
                    .setKNearestNeighborsCount(1)
                    .setFields("embedding");
            
            // Search options
            SearchOptions searchOptions = new SearchOptions()
                    .setVectorSearchOptions(new VectorSearchOptions().setQueries(vectorQuery))
                    .setTop(1)
                    .setFilter("active eq true")
                    .setIncludeTotalCount(true);

            // Execute search
            SearchPagedIterable results = rankSearchClient.search(null, searchOptions, null);
            
            Optional<RankDocument> resolvedRank = results.stream()
                    .findFirst()
                    .map(r -> r.getDocument(RankDocument.class));

            if (resolvedRank.isPresent()) {
                log.info("Successfully resolved rank: {} for client description", resolvedRank.get().getId());
                return resolvedRank.get();
            } else {
                log.warn("No rank resolved for client description");
                return null;
            }

        } catch (Exception e) {
            log.error("Error resolving rank for client description: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Searches for products using rank and amount criteria via semantic search.
     * 
     * @param rankId The resolved rank ID
     * @param requestedAmount The amount requested by customer
     * @return List of matching product documents with relevance scores
     */
    public List<ProductSearchResult> searchProductsByRankAndAmount(String rankId, BigDecimal requestedAmount) {
        String productSemanticQuery = String.format("Cliente con Rank %s solicita un crédito de %s soles", 
                rankId, requestedAmount);
        
        log.info("Searching products using semantic query: {}", productSemanticQuery);
        
        try {
            // Generate embeddings for product search query
            List<Float> queryEmbedding = generateEmbeddings(productSemanticQuery);
            
            // Create vector query
            VectorizedQuery vectorQuery = new VectorizedQuery(queryEmbedding)
                    .setKNearestNeighborsCount(10)
                    .setFields("embedding");
            
            // Build filter for active products and amount range
            String filter = String.format("active eq true and minimumAmount le %s and maximumAmount ge %s", 
                    requestedAmount, requestedAmount);
            
            // Search options
            SearchOptions searchOptions = new SearchOptions()
                    .setVectorSearchOptions(new VectorSearchOptions().setQueries(vectorQuery))
                    .setTop(10)
                    .setFilter(filter)
                    .setIncludeTotalCount(true);

            // Execute search
            SearchPagedIterable results = productSearchClient.search(null, searchOptions, null);
            
            List<ProductSearchResult> productResults = results.stream()
                    .map(this::mapToProductSearchResult)
                    .collect(Collectors.toList());

            log.info("Found {} products matching criteria for rank {} and amount {}", 
                    productResults.size(), rankId, requestedAmount);
            
            return productResults;

        } catch (Exception e) {
            log.error("Error searching products by rank and amount: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Generates embeddings for text using OpenAI.
     */
    private List<Float> generateEmbeddings(String text) {
        try {
            log.debug("Generating embeddings for text: {}", text);
            
            // Validate text input
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("Text cannot be null or empty");
            }
            
            // Create embeddings options
            EmbeddingsOptions options = new EmbeddingsOptions(List.of(text.trim()));
            options.setUser("ai-search-evaluation-system");
            options.setInputType("text");
            
            // Call OpenAI
            Embeddings embeddings = openAIClient.getEmbeddings(
                    azureProperties.getOpenai().getEmbeddingModel(),
                    options
            );

            List<Float> embeddingVector = embeddings.getData().get(0).getEmbedding()
                    .stream()
                    .map(Double::floatValue)
                    .collect(Collectors.toList());
            
            log.debug("Generated embeddings vector with {} dimensions", embeddingVector.size());
            return embeddingVector;

        } catch (Exception e) {
            log.error("Error generating embeddings for text '{}': {}", text, e.getMessage(), e);
            throw new RuntimeException("Failed to generate embeddings", e);
        }
    }

    /**
     * Maps Azure Search result to ProductSearchResult.
     */
    private ProductSearchResult mapToProductSearchResult(com.azure.search.documents.models.SearchResult searchResult) {
        ProductDocument product = searchResult.getDocument(ProductDocument.class);
        Double score = searchResult.getScore();
        Double relevanceScore = score != null ? score : 0.0;
        
        return new ProductSearchResult(product, relevanceScore);
    }

    /**
     * Data class for product search results with relevance score.
     */
    public static class ProductSearchResult {
        private final ProductDocument product;
        private final Double relevanceScore;

        public ProductSearchResult(ProductDocument product, Double relevanceScore) {
            this.product = product;
            this.relevanceScore = relevanceScore;
        }

        public ProductDocument getProduct() {
            return product;
        }

        public Double getRelevanceScore() {
            return relevanceScore;
        }
    }
}