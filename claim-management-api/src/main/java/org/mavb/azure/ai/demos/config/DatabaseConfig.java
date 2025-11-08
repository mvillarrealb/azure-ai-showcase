package org.mavb.azure.ai.demos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuración de base de datos para PostgreSQL y JPA.
 * Habilita auditoría JPA, repositorios y gestión de transacciones.
 */
@Configuration
@EnableJpaRepositories(basePackages = "org.mavb.azure.ai.demos.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {
    // Esta clase puede expandirse con beans de configuración adicionales si es necesario
}