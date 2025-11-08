package org.mavb.azure.ai.demos.exception;

import org.junit.jupiter.api.Test;
import org.mavb.azure.ai.demos.dto.response.ErrorResponseDto;
import org.mavb.azure.ai.demos.dto.response.ValidationErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test para verificar que el GlobalExceptionHandler funciona correctamente con WebFlux
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleNotFoundExceptionReturns404() {
        // Given
        String errorMessage = "Transacción no encontrada";
        NotFoundException exception = new NotFoundException(errorMessage);

        // When
        ResponseEntity<ErrorResponseDto> response = handler.handleNotFound(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Datos no encontrados", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals("NOT_FOUND", response.getBody().getCode());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleWebExchangeBindExceptionReturns400() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("transaction", "amount", "El monto es requerido");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        
        WebExchangeBindException exception = mock(WebExchangeBindException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(exception.getMessage()).thenReturn("Validation failed");

        // When
        ResponseEntity<ValidationErrorResponseDto> response = 
                handler.handleWebExchangeBindException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Datos inválidos", response.getBody().getError());
        assertEquals("Error en la validación de campos", response.getBody().getMessage());
        assertEquals("VALIDATION_ERROR", response.getBody().getCode());
        assertNotNull(response.getBody().getFieldErrors());
        assertEquals("El monto es requerido", response.getBody().getFieldErrors().get("amount"));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleProcessingExceptionReturns500() {
        // Given
        String errorMessage = "Error procesando archivo";
        ProcessingException exception = new ProcessingException(errorMessage);

        // When
        ResponseEntity<ErrorResponseDto> response = handler.handleProcessingException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error de procesamiento", response.getBody().getError());
        assertEquals("Ocurrió un error durante el procesamiento de la solicitud", response.getBody().getMessage());
        assertEquals("PROCESSING_ERROR", response.getBody().getCode());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleGenericExceptionReturns500() {
        // Given
        Exception exception = new RuntimeException("Error inesperado");

        // When
        ResponseEntity<ErrorResponseDto> response = handler.handleGenericException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error interno del servidor", response.getBody().getError());
        assertEquals("Ocurrió un error inesperado", response.getBody().getMessage());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getCode());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleValidationExceptionReturns400() {
        // Given
        String errorMessage = "La categoría no puede estar vacía";
        ValidationException exception = new ValidationException(errorMessage);

        // When
        ResponseEntity<ErrorResponseDto> response = handler.handleValidationException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Datos inválidos", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals("BUSINESS_VALIDATION_ERROR", response.getBody().getCode());
        assertNotNull(response.getBody().getTimestamp());
    }
}