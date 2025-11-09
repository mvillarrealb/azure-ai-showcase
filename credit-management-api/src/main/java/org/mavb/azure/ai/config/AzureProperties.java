package org.mavb.azure.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "azure")
public class AzureProperties {
    private OpenAI openai = new OpenAI();
    private Search search = new Search();

    @Data
    public static class OpenAI {
        private String endpoint;
        private String key;
        private String embeddingModel;
    }

    @Data
    public static class Search {
        private String endpoint;
        private String key;
        private Indices indices = new Indices();

        @Data
        public static class Indices {
            private String ranks;
            private String products;
        }
    }
}