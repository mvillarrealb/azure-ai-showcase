package org.mavb.azure.ai.demos.exception;

/**
 * Excepción para errores de procesamiento (HTTP 500)
 */
public class ProcessingException extends RuntimeException {
    
    public ProcessingException(String message) {
        super(message);
    }
    
    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}