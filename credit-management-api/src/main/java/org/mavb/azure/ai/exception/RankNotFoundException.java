package org.mavb.azure.ai.exception;

/**
 * Excepción lanzada cuando no se encuentra un rango
 * con el ID especificado en la base de datos.
 */
public class RankNotFoundException extends RuntimeException {

    public RankNotFoundException(String message) {
        super(message);
    }

    public RankNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}