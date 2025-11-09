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
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class EvaluationController {

    private final EvaluationService evaluationService;

    /**
     * Evaluates client credit eligibility using AI Search.
     *
     * @param request Evaluation request data
     * @return Credit evaluation response
     */
    @PostMapping("/evaluate")
    public ResponseEntity<EvaluationResponseDTO> evaluateClientEligibility(
            @Valid @RequestBody EvaluationRequestDTO request) {

        log.debug("POST /products/evaluate - Starting AI Search evaluation for customer: {}", 
                request.getIdentityDocument());

        EvaluationResponseDTO response = evaluationService.evaluateClientEligibility(request);

        log.info("AI Search evaluation completed for customer: {}, eligible products: {}, semantic rank: {}",
                request.getIdentityDocument(),
                response.getEligibleProducts().size(),
                response.getClientProfile().getSemanticRank());

        return ResponseEntity.ok(response);
    }
}