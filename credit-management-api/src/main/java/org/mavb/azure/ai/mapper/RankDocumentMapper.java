package org.mavb.azure.ai.mapper;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.Embeddings;
import com.azure.ai.openai.models.EmbeddingsOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.config.AzureProperties;
import org.mavb.azure.ai.dto.RankUploadDto;
import org.mavb.azure.ai.entity.RankDocument;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
     * Maps rank DTO to a RankDocument with generated embeddings.
     * Uses a blocking I/O scheduler to avoid blocking reactive threads.
     *
     * @param rankDto The complete rank upload DTO
     * @return Mono<RankDocument> with embeddings
     */
    public Mono<RankDocument> toRankDocument(RankUploadDto rankDto) {
        log.debug("Mapping rank DTO to RankDocument: {}", rankDto.getId());

        return generateEmbeddings(rankDto.getDescription())
                .map(embeddings -> RankDocument.builder()
                        .id(rankDto.getId())
                        .name(rankDto.getName())
                        .description(rankDto.getDescription())
                        .embedding(embeddings)
                        .build())
                .onErrorResume(e -> {
                    log.error("Error generating embeddings for rank {}: {}", rankDto.getId(), e.getMessage(), e);
                    return Mono.just(createBasicDocument(rankDto));
                });
    }

    /**
     * Legacy method for backward compatibility - DEPRECATED
     * @deprecated Use {@link #toRankDocument(RankUploadDto)} instead
     */
    @Deprecated
    public Mono<RankDocument> toRankDocument(String id, String name, String description) {
        RankUploadDto dto = new RankUploadDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        return toRankDocument(dto);
    }

    /**
     * Synchronous version for backward compatibility - DEPRECATED
     * @deprecated Use reactive version instead
     */
    @Deprecated
    public RankDocument toRankDocumentSync(String id, String name, String description) {
        RankUploadDto dto = new RankUploadDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        return toRankDocumentSync(dto);
    }

    /**
     * Synchronous version for specific use cases.
     * Note: This should not be called from reactive threads.
     */
    public RankDocument toRankDocumentSync(RankUploadDto rankDto) {
        log.debug("Mapping rank DTO to RankDocument (sync): {}", rankDto.getId());

        try {
            List<Float> embeddings = generateEmbeddingsSync(rankDto.getDescription());

            return RankDocument.builder()
                    .id(rankDto.getId())
                    .name(rankDto.getName())
                    .description(rankDto.getDescription())
                    .embedding(embeddings)
                    .build();

        } catch (Exception e) {
            log.error("Error generating embeddings for rank {}: {}", rankDto.getId(), e.getMessage(), e);
            return createBasicDocument(rankDto);
        }
    }

    /**
     * Generates embeddings for the rank description using OpenAI (Reactive).
     * Runs on a blocking I/O scheduler to avoid blocking reactive threads.
     */
    private Mono<List<Float>> generateEmbeddings(String description) {
        return Mono.fromCallable(() -> generateEmbeddingsSync(description))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(subscription -> log.debug("Generating embeddings for rank description (reactive)"))
                .doOnSuccess(result -> log.debug("Generated embeddings vector with {} dimensions", result.size()));
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
    private RankDocument createBasicDocument(RankUploadDto rankDto) {
        return RankDocument.builder()
                .id(rankDto.getId())
                .name(rankDto.getName())
                .description(rankDto.getDescription())
                .embedding(List.of())
                .build();
    }

    /**
     * Legacy method for creating basic documents - DEPRECATED
     * @deprecated Use {@link #createBasicDocument(RankUploadDto)} instead
     */
    @Deprecated
    private RankDocument createBasicDocument(String id, String name, String description) {
        RankUploadDto dto = new RankUploadDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        return createBasicDocument(dto);
    }
}