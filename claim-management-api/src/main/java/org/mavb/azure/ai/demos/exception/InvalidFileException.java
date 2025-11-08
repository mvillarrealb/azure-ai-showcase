package org.mavb.azure.ai.demos.exception;

/**
 * Excepción lanzada cuando el archivo de importación no es válido.
 */
public class InvalidFileException extends RuntimeException {

    public InvalidFileException(String message) {
        super(message);
    }

    public InvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }
}