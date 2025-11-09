package org.mavb.azure.ai.exception;

/**
 * Excepción lanzada cuando se intenta crear un rango
 * con un ID que ya existe en la base de datos.
 */
public class RankAlreadyExistsException extends RuntimeException {

    public RankAlreadyExistsException(String message) {
        super(message);
    }

    public RankAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}