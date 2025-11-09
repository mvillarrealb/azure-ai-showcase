package org.mavb.azure.ai.mapper;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.Embeddings;
import com.azure.ai.openai.models.EmbeddingsOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.config.AzureProperties;
import org.mavb.azure.ai.entity.RankDocument;
import org.mavb.azure.ai.entity.RankEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper component for converting RankEntity to RankDocument.
 * Includes embedding generation for semantic rank classification.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RankDocumentMapper {

    private final OpenAIClient openAIClient;
    private final AzureProperties azureProperties;

    /**
     * Synchronous version for JPA listeners.
     * Converts RankEntity to RankDocument with embeddings.
     */
    public RankDocument toRankDocumentSync(RankEntity rankEntity) {
        log.debug("Mapping rank entity to RankDocument (sync): {}", rankEntity.getId());

        try {
            List<Float> embeddings = generateEmbeddingsSync(rankEntity.getDescription());

            return RankDocument.builder()
                    .id(rankEntity.getId())
                    .name(rankEntity.getName())
                    .description(rankEntity.getDescription())
                    .embedding(embeddings)
                    .build();

        } catch (Exception e) {
            log.error("Error generating embeddings for rank {}: {}", rankEntity.getId(), e.getMessage(), e);
            return createBasicDocument(rankEntity);
        }
    }

    /**
     * Generates embeddings for the rank description using OpenAI (Synchronous).
     */
    private List<Float> generateEmbeddingsSync(String description) {
        try {
            log.info("🔍 Generating embeddings for: '{}'", description);
            
            EmbeddingsOptions options = new EmbeddingsOptions(List.of(description));
            options.setUser("credit-system");
            options.setInputType("text");
            
            Embeddings embeddings = openAIClient.getEmbeddings(
                    azureProperties.getOpenai().getEmbeddingModel(),
                    options
            );
            
            List<Float> result = embeddings.getData().get(0)
                    .getEmbedding()
                    .stream()
                    .map(Double::floatValue)
                    .toList();

            log.info("✅ Generated {} embeddings", result.size());
            return result;

        } catch (Exception e) {
            log.error("❌ Embeddings failed: {}", e.getMessage(), e);
            throw new RuntimeException("Embeddings error: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a basic document without embeddings in case of errors.
     */
    private RankDocument createBasicDocument(RankEntity rankEntity) {
        return RankDocument.builder()
                .id(rankEntity.getId())
                .name(rankEntity.getName())
                .description(rankEntity.getDescription())
                .embedding(List.of())
                .build();
    }
}