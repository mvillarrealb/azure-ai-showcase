package org.mavb.azure.ai.mapper;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.Embeddings;
import com.azure.ai.openai.models.EmbeddingsOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.config.AzureProperties;
import org.mavb.azure.ai.entity.RankDocument;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper component for converting rank data to RankDocument.
 * Includes embedding generation for semantic rank classification.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RankDocumentMapper {

    private final OpenAIClient openAIClient;
    private final AzureProperties azureProperties;

    /**
     * Maps rank data to a RankDocument with generated embeddings.
     *
     * @param id The rank ID
     * @param name The rank name  
     * @param description The rank description
     * @return RankDocument with embeddings
     */
    public RankDocument toRankDocument(String id, String name, String description) {
        log.debug("Mapping rank data to RankDocument: {}", id);

        try {
            List<Float> embeddings = generateEmbeddings(description);

            return RankDocument.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .embedding(embeddings)
                    .build();

        } catch (Exception e) {
            log.error("Error generating embeddings for rank {}: {}", id, e.getMessage(), e);
            return createBasicDocument(id, name, description);
        }
    }

    /**
     * Generates embeddings for the rank description using OpenAI.
     */
    private List<Float> generateEmbeddings(String description) {
        try {
            log.debug("Generating embeddings for rank description");
            
            Embeddings embeddings = openAIClient.getEmbeddings(
                    azureProperties.getOpenai().getEmbeddingModel(),
                    new EmbeddingsOptions(List.of(description))
            );

            List<Float> embeddingVector = embeddings.getData().get(0)
                    .getEmbedding()
                    .stream()
                    .map(Double::floatValue)
                    .toList();

            log.debug("Generated embeddings vector with {} dimensions", embeddingVector.size());
            return embeddingVector;

        } catch (Exception e) {
            log.error("Failed to generate embeddings: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating embeddings for rank description", e);
        }
    }

    /**
     * Creates a basic document without embeddings in case of errors.
     */
    private RankDocument createBasicDocument(String id, String name, String description) {
        return RankDocument.builder()
                .id(id)
                .name(name)
                .description(description)
                .embedding(List.of())
                .build();
    }
}