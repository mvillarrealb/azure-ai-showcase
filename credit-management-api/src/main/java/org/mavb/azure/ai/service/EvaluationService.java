package org.mavb.azure.ai.service;

import org.mavb.azure.ai.dto.request.EvaluationRequestDTO;
import org.mavb.azure.ai.dto.response.EvaluationResponseDTO;
import reactor.core.publisher.Mono;

/**
 * Service interface for credit evaluation operations.
 * Defines business logic methods for customer eligibility assessment.
 */
public interface EvaluationService {

    /**
     * Evaluate customer eligibility for credit products (Reactive).
     * Performs comprehensive assessment based on customer profile and requested loan parameters.
     *
     * @param request Evaluation request containing customer information and loan requirements
     * @return Mono with evaluation response with eligible products and recommendations
     * @throws org.mavb.azure.ai.exception.EvaluationException if evaluation cannot be completed
     */
    Mono<EvaluationResponseDTO> evaluateClientEligibility(EvaluationRequestDTO request);
}