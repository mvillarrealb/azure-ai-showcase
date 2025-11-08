package org.mavb.azure.ai.demos.config;

import com.azure.ai.documentintelligence.DocumentIntelligenceClient;
import com.azure.ai.documentintelligence.DocumentIntelligenceClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "document-intelligence")
@Data
@Slf4j
public class DocumentIntelligenceConfig {

    private String key;
    private String endpoint;

    @Bean
    public DocumentIntelligenceClient documentIntelligenceClient() {
        log.info("Configurando DocumentIntelligenceClient con endpoint: {}", endpoint);
        return  new DocumentIntelligenceClientBuilder()
                .credential(new AzureKeyCredential(key))
                .endpoint(endpoint)
                .buildClient();
    }
}
