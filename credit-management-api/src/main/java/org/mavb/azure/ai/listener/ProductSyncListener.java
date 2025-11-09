package org.mavb.azure.ai.listener;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.entity.CreditProductEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * JPA Entity Listener for automatic synchronization of CreditProductEntity to Azure AI Search.
 * Simplified implementation until Azure Search SDK compatibility issues are resolved.
 */
@Component
@Slf4j
public class ProductSyncListener {

    /**
     * Called after a product is persisted to the database.
     * Logs the operation until Azure Search integration is implemented.
     */
    @PostPersist
    public void afterInsert(CreditProductEntity product) {
        log.info("Product inserted, AI Search sync planned for: {}", product.getId());
        // TODO: Implement Azure Search indexing once SDK compatibility is resolved
    }

    /**
     * Called after a product is updated in the database.
     * Logs the operation until Azure Search integration is implemented.
     */
    @PostUpdate
    public void afterUpdate(CreditProductEntity product) {
        log.info("Product updated, AI Search sync planned for: {}", product.getId());
        // TODO: Implement Azure Search indexing once SDK compatibility is resolved
    }

    /**
     * Called after a product is removed from the database.
     * Logs the operation until Azure Search integration is implemented.
     */
    @PostRemove
    public void afterDelete(CreditProductEntity product) {
        log.info("Product deleted, AI Search removal planned for: {}", product.getId());
        // TODO: Implement Azure Search removal once SDK compatibility is resolved
    }
}