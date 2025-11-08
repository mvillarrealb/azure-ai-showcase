package org.mavb.azure.ai.demos.exception;

/**
 * Excepción lanzada cuando se intenta crear un reclamo duplicado.
 */
public class DuplicateClaimException extends RuntimeException {

    public DuplicateClaimException(String message) {
        super(message);
    }

    public DuplicateClaimException(String message, Throwable cause) {
        super(message, cause);
    }
}