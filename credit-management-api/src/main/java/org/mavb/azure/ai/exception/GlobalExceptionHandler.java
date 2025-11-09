package org.mavb.azure.ai.exception;

import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.dto.response.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for Credit Management API.
 * Handles all exceptions thrown by controllers and provides structured error responses.
 * 
 * Uses @ControllerAdvice with @ResponseBody for WebFlux compatibility.
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle ProductNotFoundException.
     * Thrown when a requested credit product is not found.
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductNotFound(ProductNotFoundException ex) {
        log.warn("Product not found: {}", ex.getMessage());

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("Product Not Found")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle ProductAlreadyExistsException.
     * Thrown when attempting to create a product with an existing ID.
     */
    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductAlreadyExists(ProductAlreadyExistsException ex) {
        log.warn("Product already exists: {}", ex.getMessage());

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("Product Already Exists")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handle EvaluationException.
     * Thrown when credit evaluation process fails.
     */
    @ExceptionHandler(EvaluationException.class)
    public ResponseEntity<ErrorResponseDTO> handleEvaluationException(EvaluationException ex) {
        log.error("Credit evaluation failed: {}", ex.getMessage());

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("Evaluation Error")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handle WebExchangeBindException for WebFlux validation errors.
     * Thrown when request body validation fails in WebFlux.
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponseDTO> handleWebExchangeBindException(WebExchangeBindException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        List<ErrorResponseDTO.ErrorDetailDTO> details = buildValidationDetails(ex.getBindingResult());

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("Validation Error")
                .message("Los datos proporcionados no son válidos")
                .details(details)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle MethodArgumentNotValidException for validation errors.
     * Thrown when request body validation fails.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        List<ErrorResponseDTO.ErrorDetailDTO> details = buildValidationDetails(ex.getBindingResult());

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("Validation Error")
                .message("Los datos proporcionados no son válidos")
                .details(details)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle ConstraintViolationException for constraint validation errors.
     * Thrown when request parameter validation fails.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint validation failed: {}", ex.getMessage());

        List<ErrorResponseDTO.ErrorDetailDTO> details = ex.getConstraintViolations()
                .stream()
                .map(violation -> ErrorResponseDTO.ErrorDetailDTO.builder()
                        .field(getFieldName(violation))
                        .message(violation.getMessage())
                        .build())
                .collect(Collectors.toList());

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("Invalid Query Parameters")
                .message("Los parámetros de búsqueda son inválidos")
                .details(details)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle MethodArgumentTypeMismatchException for type mismatch errors.
     * Thrown when request parameter type conversion fails.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Type mismatch for parameter '{}': {}", ex.getName(), ex.getMessage());

        List<ErrorResponseDTO.ErrorDetailDTO> details = List.of(
                ErrorResponseDTO.ErrorDetailDTO.builder()
                        .field(ex.getName())
                        .message("Tipo de dato inválido para el parámetro " + ex.getName())
                        .build()
        );

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("Invalid Query Parameters")
                .message("Los parámetros de búsqueda son inválidos")
                .details(details)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle generic exceptions.
     * Catch-all handler for application-level exceptions only, not Spring framework exceptions.
     * Spring WebFlux exceptions are excluded to let Spring handle them naturally.
     */
    @ExceptionHandler({
        RuntimeException.class,
        IllegalArgumentException.class,
        IllegalStateException.class,
        NullPointerException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleApplicationExceptions(Exception ex) {
        // Skip Spring framework exceptions
        String className = ex.getClass().getName();
        if (className.startsWith("org.springframework.web.reactive") ||
            className.startsWith("org.springframework.web.server") ||
            ex instanceof NoResourceFoundException) {
            log.debug("Skipping Spring framework exception: {} - {}", className, ex.getMessage());
            // Re-throw to let Spring handle it
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }
            throw new RuntimeException(ex);
        }
        
        log.error("Application error occurred: {}", ex.getMessage(), ex);

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .error("Internal Server Error")
                .message("Ha ocurrido un error interno del servidor")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Build validation error details from binding result.
     */
    private List<ErrorResponseDTO.ErrorDetailDTO> buildValidationDetails(BindingResult bindingResult) {
        List<ErrorResponseDTO.ErrorDetailDTO> details = new ArrayList<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            details.add(ErrorResponseDTO.ErrorDetailDTO.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build());
        }

        return details;
    }

    /**
     * Extract field name from constraint violation.
     */
    private String getFieldName(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        return propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
    }
}