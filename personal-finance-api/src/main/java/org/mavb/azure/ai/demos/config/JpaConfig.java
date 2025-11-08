package org.mavb.azure.ai.demos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuración para habilitar auditoría automática en JPA.
 * Permite que @CreatedDate y @LastModifiedDate funcionen automáticamente.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // La configuración se hace a través de la anotación
}