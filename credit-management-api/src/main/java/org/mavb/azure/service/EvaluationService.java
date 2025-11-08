package org.mavb.azure.service;

import org.mavb.azure.dto.EvaluationRequestDTO;
import org.mavb.azure.dto.EvaluationResponseDTO;

/**
 * Service interface for credit evaluation operations.
 * Defines business logic methods for customer eligibility assessment.
 */
public interface EvaluationService {

    /**
     * Evaluate customer eligibility for credit products.
     * Performs comprehensive assessment based on customer profile and requested loan parameters.
     *
     * @param request Evaluation request containing customer information and loan requirements
     * @return Evaluation response with eligible products and recommendations
     * @throws org.mavb.azure.exception.EvaluationException if evaluation cannot be completed
     */
    EvaluationResponseDTO evaluateClientEligibility(EvaluationRequestDTO request);
}