package org.mavb.azure.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for asynchronous operations.
 * Enables async execution for product synchronization to AI Search.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Thread pool executor for AI Search synchronization operations.
     */
    @Bean(name = "aiSearchSyncExecutor")
    public Executor aiSearchSyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AISearchSync-");
        executor.initialize();
        return executor;
    }
}