package org.mavb.azure.exception;

/**
 * Exception thrown when credit evaluation process fails.
 */
public class EvaluationException extends RuntimeException {

    public EvaluationException(String message) {
        super(message);
    }

    public EvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}