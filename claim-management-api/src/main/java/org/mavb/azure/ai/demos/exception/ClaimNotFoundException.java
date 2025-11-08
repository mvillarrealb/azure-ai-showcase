package org.mavb.azure.ai.demos.exception;

/**
 * Excepción lanzada cuando no se encuentra un reclamo específico.
 */
public class ClaimNotFoundException extends RuntimeException {

    public ClaimNotFoundException(String message) {
        super(message);
    }

    public ClaimNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}