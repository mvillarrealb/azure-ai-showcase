package org.mavb.azure.ai.mapper;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.Embeddings;
import com.azure.ai.openai.models.EmbeddingsOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.config.AzureProperties;
import org.mavb.azure.ai.entity.CreditProductEntity;
import org.mavb.azure.ai.entity.ProductDocument;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper component for converting CreditProductEntity to ProductDocument.
 * Includes embedding generation for semantic search capabilities.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductDocumentMapper {

    private final OpenAIClient openAIClient;
    private final AzureProperties azureProperties;

    /**
     * Maps a CreditProductEntity to a ProductDocument.
     * Includes embedding generation for semantic search.
     *
     * @param entity The credit product entity to map
     * @return ProductDocument with embeddings
     */
    public ProductDocument toProductDocument(CreditProductEntity entity) {
        log.debug("Mapping CreditProductEntity to ProductDocument: {}", entity.getId());

        try {
            // Generate allowed ranks based on product characteristics
            List<String> allowedRanks = generateAllowedRanks(entity);

            // Generate search text for embeddings
            String searchText = buildSearchText(entity);
            
            // Generate embeddings
            List<Float> embeddings = generateEmbeddings(searchText);

            return ProductDocument.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .category(entity.getCategory())
                    .subcategory(entity.getSubcategory())
                    .minimumAmount(entity.getMinimumAmount())
                    .maximumAmount(entity.getMaximumAmount())
                    .currency(entity.getCurrency())
                    .term(entity.getTerm())
                    .minimumRate(entity.getMinimumRate())
                    .maximumRate(entity.getMaximumRate())
                    .requirements(entity.getRequirements())
                    .features(entity.getFeatures())
                    .benefits(entity.getBenefits())
                    .active(entity.getActive())
                    .allowedRanks(allowedRanks)
                    .embedding(embeddings)
                    .build();

        } catch (Exception e) {
            log.error("Error mapping CreditProductEntity to ProductDocument: {}", e.getMessage(), e);
            // Return basic document without embeddings in case of error
            return createBasicDocument(entity);
        }
    }

    /**
     * Generates allowed ranks based on product characteristics.
     * Uses business rules to determine which customer ranks can access the product.
     */
    private List<String> generateAllowedRanks(CreditProductEntity entity) {
        // Business logic to determine allowed ranks based on product characteristics
        // This is a simplified implementation - in production this would be more sophisticated
        
        if (entity.getMaximumAmount().doubleValue() >= 100000) {
            // High-value products for premium customers
            return Arrays.asList("ORO", "PLATINO", "PREMIUM");
        } else if (entity.getMaximumAmount().doubleValue() >= 50000) {
            // Mid-range products
            return Arrays.asList("PLATA", "ORO", "PLATINO", "PREMIUM");
        } else {
            // Entry-level products for all ranks
            return Arrays.asList("BRONCE", "PLATA", "ORO", "PLATINO", "PREMIUM");
        }
    }

    /**
     * Builds comprehensive search text for embedding generation.
     */
    private String buildSearchText(CreditProductEntity entity) {
        StringBuilder searchText = new StringBuilder();
        
        // Add basic product info
        searchText.append(entity.getName()).append(" ");
        searchText.append(entity.getDescription()).append(" ");
        searchText.append("Categoría: ").append(entity.getCategory()).append(" ");
        
        if (entity.getSubcategory() != null) {
            searchText.append("Subcategoría: ").append(entity.getSubcategory()).append(" ");
        }
        
        // Add financial details
        searchText.append("Moneda: ").append(entity.getCurrency()).append(" ");
        searchText.append("Monto mínimo: ").append(entity.getMinimumAmount()).append(" ");
        searchText.append("Monto máximo: ").append(entity.getMaximumAmount()).append(" ");
        
        // Add requirements, features, and benefits
        if (entity.getRequirements() != null && !entity.getRequirements().isEmpty()) {
            searchText.append("Requisitos: ").append(String.join(", ", entity.getRequirements())).append(" ");
        }
        
        if (entity.getFeatures() != null && !entity.getFeatures().isEmpty()) {
            searchText.append("Características: ").append(String.join(", ", entity.getFeatures())).append(" ");
        }
        
        if (entity.getBenefits() != null && !entity.getBenefits().isEmpty()) {
            searchText.append("Beneficios: ").append(String.join(", ", entity.getBenefits())).append(" ");
        }
        
        return searchText.toString().trim();
    }

    /**
     * Generates embeddings for the given text using OpenAI.
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
            log.error("Error generating embeddings for product {}: {}", text, e.getMessage());
            return List.of(); // Return empty embeddings on error
        }
    }

    /**
     * Creates a basic document without embeddings in case of errors.
     */
    private ProductDocument createBasicDocument(CreditProductEntity entity) {
        List<String> allowedRanks = generateAllowedRanks(entity);
        
        return ProductDocument.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .subcategory(entity.getSubcategory())
                .minimumAmount(entity.getMinimumAmount())
                .maximumAmount(entity.getMaximumAmount())
                .currency(entity.getCurrency())
                .term(entity.getTerm())
                .minimumRate(entity.getMinimumRate())
                .maximumRate(entity.getMaximumRate())
                .requirements(entity.getRequirements())
                .features(entity.getFeatures())
                .benefits(entity.getBenefits())
                .active(entity.getActive())
                .allowedRanks(allowedRanks)
                .embedding(List.of()) // Empty embeddings
                .build();
    }
}