package org.mavb.azure.ai.service.impl;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.Embeddings;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.models.*;

import com.azure.search.documents.util.SearchPagedIterable;
import lombok.*;
import org.mavb.azure.ai.config.AzureProperties;
import org.mavb.azure.ai.entity.RankDocument;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankService {

    private final OpenAIClient openAI;
    private final SearchClient rankSearch;
    private final AzureProperties azure;

    public RankService(OpenAIClient openAI, @Qualifier("rankSearchClient") SearchClient rankSearch, AzureProperties azure) {
        this.openAI = openAI;
        this.rankSearch = rankSearch;
        this.azure = azure;
    }

    public RankDocument resolveRank(String clientDescription) {
        Embeddings emb = openAI.getEmbeddings(
                azure.getOpenai().getEmbeddingModel(),
                new EmbeddingsOptions(List.of(clientDescription))
        );
        List<Float> vector = emb.getData().get(0).getEmbedding().stream().map(Double::floatValue).toList();

        VectorizedQuery vectorQuery = new VectorizedQuery(vector)
                .setKNearestNeighborsCount(1)
                .setFields("embedding");
        
        SearchOptions opts = new SearchOptions()
                .setVectorSearchOptions(new VectorSearchOptions().setQueries(vectorQuery))
                .setTop(1);

        SearchPagedIterable results = rankSearch.search(null, opts, null);
        return results.stream().findFirst().map(r -> r.getDocument(RankDocument.class)).orElse(null);
    }
}