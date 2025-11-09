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
@RequiredArgsConstructor
public class RankService {

    private final OpenAIClient openAI;
    @Qualifier("rankSearchClient")
    private final SearchClient rankSearch;
    private final AzureProperties azure;

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