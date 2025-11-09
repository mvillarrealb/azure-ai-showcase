package org.mavb.azure.ai.demos.exception;

import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.demos.dto.response.ErrorResponseDto;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la aplicación.
 */
@ControllerAdvice
@Order(-1)
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ClaimNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleClaimNotFound(ClaimNotFoundException ex) {
        log.error("Reclamo no encontrado: {}", ex.getMessage());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .error("Claim Not Found")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidFileException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleInvalidFile(InvalidFileException ex) {
        log.error("Archivo inválido: {}", ex.getMessage());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .error("Invalid File")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DuplicateClaimException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleDuplicateClaim(DuplicateClaimException ex) {
        log.error("Reclamo duplicado: {}", ex.getMessage());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .error("Duplicate Claim")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.error("Error de validación: {}", ex.getMessage());
        
        List<ErrorResponseDto.FieldErrorDto> fieldErrors = new ArrayList<>();
        
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(ErrorResponseDto.FieldErrorDto.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build());
        }
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .error("Validation Error")
                .message("Los datos proporcionados no son válidos")
                .details(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Error de constraint violation: {}", ex.getMessage());
        
        List<ErrorResponseDto.FieldErrorDto> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(violation -> ErrorResponseDto.FieldErrorDto.builder()
                        .field(getFieldNameFromPath(violation))
                        .message(violation.getMessage())
                        .build())
                .collect(Collectors.toList());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .error("Validation Error")
                .message("Los parámetros proporcionados no son válidos")
                .details(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        log.error("Archivo demasiado grande: {}", ex.getMessage());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .error("File Too Large")
                .message("El archivo es demasiado grande. Tamaño máximo permitido: 10MB")
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Argumento ilegal: {}", ex.getMessage());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .error("Invalid Argument")
                .message("Parámetro inválido: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .error("Internal Server Error")
                .message("Ha ocurrido un error inesperado. Por favor, inténtelo más tarde.")
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private String getFieldNameFromPath(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        String[] parts = propertyPath.split("\\.");
        return parts.length > 0 ? parts[parts.length - 1] : propertyPath;
    }
}