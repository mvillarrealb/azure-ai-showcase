package org.mavb.azure.ai.service.impl;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.Embeddings;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.models.*;
import com.azure.search.documents.util.SearchPagedIterable;
import lombok.*;
import org.mavb.azure.ai.config.AzureProperties;
import org.mavb.azure.ai.entity.ProductDocument;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final OpenAIClient openAI;
    private final SearchClient productSearch;
    private final AzureProperties azure;

    public ProductService(OpenAIClient openAI, @Qualifier("productSearchClient") SearchClient productSearch, AzureProperties azure) {
        this.openAI = openAI;
        this.productSearch = productSearch;
        this.azure = azure;
    }

    public List<ProductDocument> searchProducts(String rank, String contextText) {
        Embeddings emb = openAI.getEmbeddings(
                azure.getOpenai().getEmbeddingModel(),
                new EmbeddingsOptions(List.of(contextText))
        );
        List<Float> vector = emb.getData().get(0).getEmbedding().stream().map(Double::floatValue).toList();

        String filter = "search.in(allowedRanks, '" + rank + "')";
        VectorizedQuery vectorQuery = new VectorizedQuery(vector)
                .setKNearestNeighborsCount(5)
                .setFields("embedding");

        SearchOptions opts = new SearchOptions()
                .setVectorSearchOptions(new VectorSearchOptions().setQueries(vectorQuery))
                .setFilter(filter)
                .setTop(5);

        SearchPagedIterable results = productSearch.search(null, opts, null);
        return results.stream().map(r -> r.getDocument(ProductDocument.class)).toList();
    }
}