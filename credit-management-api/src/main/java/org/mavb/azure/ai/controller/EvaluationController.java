package org.mavb.azure.ai.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.dto.request.EvaluationRequestDTO;
import org.mavb.azure.ai.dto.response.EvaluationResponseDTO;
import org.mavb.azure.ai.service.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for credit evaluation operations.
 * Handles HTTP requests for customer credit eligibility assessment.
 * 
 * Base path: /products
 * 
 * Endpoints:
 * - POST /products/evaluate - Evalúa elegibilidad del cliente para productos crediticios
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class EvaluationController {

    private final EvaluationService evaluationService;

    /**
     * Evaluar elegibilidad del cliente para productos crediticios.
     * Evalúa la elegibilidad de un cliente para productos crediticios basado en su perfil financiero.
     *
     * @param request Datos del cliente y parámetros del crédito solicitado
     * @return Respuesta con productos elegibles y recomendaciones
     */
    @PostMapping("/evaluate")
    public ResponseEntity<EvaluationResponseDTO> evaluateClientEligibility(
            @Valid @RequestBody EvaluationRequestDTO request) {

        log.debug("POST /products/evaluate - Starting evaluation for customer: {}", 
                request.getIdentityDocument());

        EvaluationResponseDTO response = evaluationService.evaluateClientEligibility(request);

        log.info("Credit evaluation completed for customer: {}, eligible products: {}, risk level: {}",
                request.getIdentityDocument(),
                response.getEligibleProducts().size(),
                response.getClientProfile().getRiskLevel());

        return ResponseEntity.ok(response);
    }
}