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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for semantic product search using Azure AI Search.
 * Provides advanced search capabilities with vector similarity and filtering.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {

    private final OpenAIClient openAIClient;
    @Qualifier("productSearchClient")
    private final SearchClient productSearchClient;
    private final AzureProperties azureProperties;

    /**
     * Search for products using semantic similarity and filters.
     */
    public List<ProductSearchResult> searchProducts(String customerRank, BigDecimal requestedAmount, String currency, String searchText) {
        log.debug("Searching products for rank: {}, amount: {}, currency: {}", customerRank, requestedAmount, currency);

        try {
            SearchOptions searchOptions = buildSearchOptions(customerRank, requestedAmount, currency, searchText);
            SearchPagedIterable results = productSearchClient.search(searchText, searchOptions, null);

            return results.stream()
                    .map(this::mapSearchResult)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error searching products in AI Search: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Search for products by rank and needs using semantic search.
     */
    public List<ProductSearchResult> searchByRankAndNeeds(String customerRank, BigDecimal requestedAmount, String customerNeeds) {
        log.debug("Searching products by rank: {} and needs: {}", customerRank, customerNeeds);

        try {
            List<Float> needsEmbedding = generateEmbeddings(customerNeeds);
            
            VectorizedQuery vectorQuery = new VectorizedQuery(needsEmbedding)
                    .setKNearestNeighborsCount(10)
                    .setFields("embedding");

            String filter = buildRankAndAmountFilter(customerRank, requestedAmount);

            SearchOptions searchOptions = new SearchOptions()
                    .setVectorSearchOptions(new VectorSearchOptions().setQueries(vectorQuery))
                    .setFilter(filter)
                    .setTop(10)
                    .setIncludeTotalCount(true);

            SearchPagedIterable results = productSearchClient.search(null, searchOptions, null);

            return results.stream()
                    .map(this::mapSearchResult)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error in semantic product search: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Builds search options with filters and vector queries.
     */
    private SearchOptions buildSearchOptions(String customerRank, BigDecimal requestedAmount, String currency, String searchText) {
        SearchOptions options = new SearchOptions()
                .setTop(10)
                .setIncludeTotalCount(true);

        String filter = buildComprehensiveFilter(customerRank, requestedAmount, currency);
        if (!filter.isEmpty()) {
            options.setFilter(filter);
        }

        if (searchText != null && !searchText.trim().isEmpty()) {
            List<Float> searchEmbedding = generateEmbeddings(searchText);
            VectorizedQuery vectorQuery = new VectorizedQuery(searchEmbedding)
                    .setKNearestNeighborsCount(5)
                    .setFields("embedding");

            options.setVectorSearchOptions(new VectorSearchOptions().setQueries(vectorQuery));
        }

        return options;
    }

    /**
     * Builds comprehensive filter for products.
     */
    private String buildComprehensiveFilter(String customerRank, BigDecimal requestedAmount, String currency) {
        StringBuilder filter = new StringBuilder();

        filter.append("active eq true");

        if (customerRank != null && !customerRank.trim().isEmpty()) {
            filter.append(" and allowedRanks/any(r: r eq '").append(customerRank).append("')");
        }

        if (requestedAmount != null) {
            filter.append(" and minimumAmount le ").append(requestedAmount);
            filter.append(" and maximumAmount ge ").append(requestedAmount);
        }

        if (currency != null && !currency.trim().isEmpty()) {
            filter.append(" and currency eq '").append(currency).append("'");
        }

        return filter.toString();
    }

    /**
     * Builds filter for rank and amount only.
     */
    private String buildRankAndAmountFilter(String customerRank, BigDecimal requestedAmount) {
        StringBuilder filter = new StringBuilder();

        filter.append("active eq true");

        if (customerRank != null && !customerRank.trim().isEmpty()) {
            filter.append(" and allowedRanks/any(r: r eq '").append(customerRank).append("')");
        }

        if (requestedAmount != null) {
            filter.append(" and minimumAmount le ").append(requestedAmount);
            filter.append(" and maximumAmount ge ").append(requestedAmount);
        }

        return filter.toString();
    }

    /**
     * Generates embeddings for text using OpenAI.
     */
    private List<Float> generateEmbeddings(String text) {
        try {
            Embeddings embeddings = openAIClient.getEmbeddings(
                    azureProperties.getOpenai().getEmbeddingModel(),
                    new EmbeddingsOptions(List.of(text))
            );

            return embeddings.getData().get(0).getEmbedding()
                    .stream()
                    .map(Double::floatValue)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error generating embeddings for search text: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Maps Azure Search result to ProductSearchResult.
     */
    private ProductSearchResult mapSearchResult(com.azure.search.documents.models.SearchResult searchResult) {
        ProductDocument product = searchResult.getDocument(ProductDocument.class);
        Double score = searchResult.getScore();

        return new ProductSearchResult(product, score != null ? score : 0.0);
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