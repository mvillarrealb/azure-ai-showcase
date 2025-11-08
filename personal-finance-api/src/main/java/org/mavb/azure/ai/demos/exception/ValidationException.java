package org.mavb.azure.ai.demos.exception;

/**
 * Excepción para errores de validación de negocio (HTTP 400)
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}