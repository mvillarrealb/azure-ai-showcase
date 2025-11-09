package org.mavb.azure.ai.config;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;

@Configuration
@RequiredArgsConstructor
public class AzureClientsConfig {

    private final AzureProperties azure;

    @Bean
    public OpenAIClient openAIClient() {
        return new OpenAIClientBuilder()
                .endpoint(azure.getOpenai().getEndpoint())
                .credential(new AzureKeyCredential(azure.getOpenai().getKey()))
                .buildClient();
    }

    @Bean(name = "rankSearchClient")
    public SearchClient rankSearchClient() {
        return new SearchClientBuilder()
                .endpoint(azure.getSearch().getEndpoint())
                .credential(new AzureKeyCredential(azure.getSearch().getKey()))
                .indexName(azure.getSearch().getIndices().getRanks())
                .buildClient();
    }

    @Bean(name = "productSearchClient")
    public SearchClient productSearchClient() {
        return new SearchClientBuilder()
                .endpoint(azure.getSearch().getEndpoint())
                .credential(new AzureKeyCredential(azure.getSearch().getKey()))
                .indexName(azure.getSearch().getIndices().getProducts())
                .buildClient();
    }
}
