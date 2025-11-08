package org.mavb.azure.ai.demos.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.demos.dto.response.ErrorResponseDto;
import org.mavb.azure.ai.demos.dto.response.ValidationErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones siguiendo los esquemas de error del OpenAPI.
 * Compatible con Spring WebFlux usando @ControllerAdvice + @ResponseBody.
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Maneja excepciones de recursos no encontrados (404)
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(NotFoundException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        
        ErrorResponseDto error = ErrorResponseDto.builder()
            .error("Datos no encontrados")
            .message(ex.getMessage())
            .code("NOT_FOUND")
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Maneja errores de validación de argumentos del método (400) - WebMVC
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleValidation(
            MethodArgumentNotValidException ex) {
        log.warn("Error de validación en request (WebMVC): {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        
        ValidationErrorResponseDto error = ValidationErrorResponseDto.builder()
            .error("Datos inválidos")
            .message("Error en la validación de campos")
            .code("VALIDATION_ERROR")
            .fieldErrors(fieldErrors)
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Maneja errores de validación en WebFlux - WebExchangeBindException (400)
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleWebExchangeBindException(
            WebExchangeBindException ex) {
        log.warn("Error de validación en request (WebFlux): {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        
        ValidationErrorResponseDto error = ValidationErrorResponseDto.builder()
            .error("Datos inválidos")
            .message("Error en la validación de campos")
            .code("VALIDATION_ERROR")
            .fieldErrors(fieldErrors)
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Maneja violaciones de constraint (400)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolation(
            ConstraintViolationException ex) {
        log.warn("Error de constraint violation: {}", ex.getMessage());
        
        ErrorResponseDto error = ErrorResponseDto.builder()
            .error("Datos inválidos")
            .message(ex.getMessage())
            .code("CONSTRAINT_VIOLATION")
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Maneja errores de validación de negocio (400)
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(ValidationException ex) {
        log.warn("Error de validación de negocio: {}", ex.getMessage());
        
        ErrorResponseDto error = ErrorResponseDto.builder()
            .error("Datos inválidos")
            .message(ex.getMessage())
            .code("BUSINESS_VALIDATION_ERROR")
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Maneja errores de procesamiento (500)
     */
    @ExceptionHandler(ProcessingException.class)
    public ResponseEntity<ErrorResponseDto> handleProcessingException(ProcessingException ex) {
        log.error("Error de procesamiento: {}", ex.getMessage(), ex);
        
        ErrorResponseDto error = ErrorResponseDto.builder()
            .error("Error de procesamiento")
            .message("Ocurrió un error durante el procesamiento de la solicitud")
            .code("PROCESSING_ERROR")
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    /**
     * Maneja errores genéricos no controlados (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        log.error("Error no controlado: {}", ex.getMessage(), ex);
        
        ErrorResponseDto error = ErrorResponseDto.builder()
            .error("Error interno del servidor")
            .message("Ocurrió un error inesperado")
            .code("INTERNAL_SERVER_ERROR")
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    /**
     * Maneja errores de argumentos ilegales (400)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Argumento ilegal: {}", ex.getMessage());
        
        ErrorResponseDto error = ErrorResponseDto.builder()
            .error("Datos inválidos")
            .message(ex.getMessage())
            .code("INVALID_ARGUMENT")
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.badRequest().body(error);
    }
}