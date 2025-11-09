package org.mavb.azure.ai.exception;

/**
 * Excepción lanzada cuando se intenta crear un producto crediticio
 * con un ID que ya existe en la base de datos.
 */
public class ProductAlreadyExistsException extends RuntimeException {

    public ProductAlreadyExistsException(String message) {
        super(message);
    }

    public ProductAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}