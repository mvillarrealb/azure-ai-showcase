package org.mavb.azure.ai.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.dto.request.EvaluationRequestDTO;
import org.mavb.azure.ai.dto.response.EvaluationResponseDTO;
import org.mavb.azure.ai.entity.CreditProductEntity;
import org.mavb.azure.ai.entity.CustomerEntity;
import org.mavb.azure.ai.exception.EvaluationException;
import org.mavb.azure.ai.repository.CreditProductRepository;
import org.mavb.azure.ai.repository.CustomerRepository;
import org.mavb.azure.ai.service.EvaluationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementation of EvaluationService interface.
 * Provides comprehensive credit evaluation logic including risk assessment and product matching.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EvaluationServiceImpl implements EvaluationService {

    private final CreditProductRepository creditProductRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public EvaluationResponseDTO evaluateClientEligibility(EvaluationRequestDTO request) {
        log.debug("Starting credit evaluation for customer: {}", request.getIdentityDocument());

        try {
            // Get or create customer profile
            CustomerEntity customer = getOrCreateCustomer(request);
            
            // Update customer information from request
            updateCustomerFromRequest(customer, request);
            
            // Calculate risk level and approved amount
            CustomerEntity.RiskLevel riskLevel = calculateRiskLevel(customer);
            customer.setRiskLevel(riskLevel);
            customerRepository.save(customer);
            
            BigDecimal approvedAmount = calculateApprovedAmount(customer, request.getRequestedAmount());
            
            // Find eligible products
            List<CreditProductEntity> eligibleProducts = creditProductRepository.findEligibleProducts(
                    request.getRequestedAmount(),
                    request.getRequestedCurrency(),
                    request.getCategory()
            );
            
            // Evaluate each product
            List<EvaluationResponseDTO.EligibleProductDTO> evaluatedProducts = 
                    evaluateProducts(eligibleProducts, customer, approvedAmount);
            
            // Build client profile
            EvaluationResponseDTO.ClientProfileDTO clientProfile = buildClientProfile(
                    customer, approvedAmount);
            
            // Build evaluation summary
            EvaluationResponseDTO.EvaluationSummaryDTO summary = buildSummary(
                    evaluatedProducts, LocalDateTime.now());
            
            EvaluationResponseDTO response = EvaluationResponseDTO.builder()
                    .clientProfile(clientProfile)
                    .eligibleProducts(evaluatedProducts)
                    .summary(summary)
                    .build();
            
            log.info("Credit evaluation completed for customer: {}, eligible products: {}", 
                    request.getIdentityDocument(), evaluatedProducts.size());
            
            return response;
            
        } catch (Exception e) {
            log.error("Error during credit evaluation for customer: {}", 
                    request.getIdentityDocument(), e);
            throw new EvaluationException("Error durante la evaluación crediticia: " + e.getMessage());
        }
    }

    private CustomerEntity getOrCreateCustomer(EvaluationRequestDTO request) {
        return customerRepository.findByIdentityDocumentAndActiveTrue(request.getIdentityDocument())
                .orElse(CustomerEntity.builder()
                        .identityDocument(request.getIdentityDocument())
                        .active(true)
                        .build());
    }

    private void updateCustomerFromRequest(CustomerEntity customer, EvaluationRequestDTO request) {
        if (request.getAdditionalInfo() != null) {
            EvaluationRequestDTO.AdditionalInfoDTO info = request.getAdditionalInfo();
            
            if (info.getMonthlyIncome() != null) {
                customer.setMonthlyIncome(info.getMonthlyIncome());
            }
            if (info.getCurrentDebt() != null) {
                customer.setCurrentDebt(info.getCurrentDebt());
            }
            if (info.getCreditScore() != null) {
                customer.setCreditScore(info.getCreditScore());
            }
            if (info.getEmploymentType() != null) {
                customer.setEmploymentType(CustomerEntity.EmploymentType.valueOf(
                        info.getEmploymentType().toUpperCase()));
            }
        }
        
        // Set default values if missing
        if (customer.getCreditScore() == null) {
            customer.setCreditScore(600); // Default moderate score
        }
        if (customer.getCurrentDebt() == null) {
            customer.setCurrentDebt(BigDecimal.ZERO);
        }
        if (customer.getEmploymentType() == null) {
            customer.setEmploymentType(CustomerEntity.EmploymentType.DEPENDIENTE);
        }
    }

    private CustomerEntity.RiskLevel calculateRiskLevel(CustomerEntity customer) {
        int score = 0;
        
        // Credit score evaluation (40% weight)
        if (customer.getCreditScore() >= 750) score += 40;
        else if (customer.getCreditScore() >= 650) score += 30;
        else if (customer.getCreditScore() >= 550) score += 20;
        else score += 10;
        
        // Debt-to-income ratio evaluation (30% weight)
        if (customer.getMonthlyIncome() != null && customer.getMonthlyIncome().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal debtRatio = customer.getCurrentDebt()
                    .divide(customer.getMonthlyIncome(), 4, RoundingMode.HALF_UP);
            
            if (debtRatio.compareTo(BigDecimal.valueOf(0.3)) <= 0) score += 30;
            else if (debtRatio.compareTo(BigDecimal.valueOf(0.5)) <= 0) score += 20;
            else if (debtRatio.compareTo(BigDecimal.valueOf(0.7)) <= 0) score += 10;
            else score += 0;
        } else {
            score += 15; // Moderate score if income not available
        }
        
        // Employment type evaluation (30% weight)
        switch (customer.getEmploymentType()) {
            case DEPENDIENTE -> score += 30;
            case EMPRESARIO -> score += 25;
            case INDEPENDIENTE -> score += 20;
        }
        
        // Determine risk level based on total score
        if (score >= 80) return CustomerEntity.RiskLevel.BAJO;
        else if (score >= 60) return CustomerEntity.RiskLevel.MEDIO;
        else return CustomerEntity.RiskLevel.ALTO;
    }

    private BigDecimal calculateApprovedAmount(CustomerEntity customer, BigDecimal requestedAmount) {
        BigDecimal approvedAmount = requestedAmount;
        
        // Reduce approved amount based on risk level
        switch (customer.getRiskLevel()) {
            case ALTO -> approvedAmount = approvedAmount.multiply(BigDecimal.valueOf(0.6));
            case MEDIO -> approvedAmount = approvedAmount.multiply(BigDecimal.valueOf(0.8));
            case BAJO -> approvedAmount = approvedAmount.multiply(BigDecimal.valueOf(0.9));
        }
        
        // Apply income-based limits if available
        if (customer.getMonthlyIncome() != null) {
            BigDecimal maxByIncome = customer.getMonthlyIncome().multiply(BigDecimal.valueOf(5));
            approvedAmount = approvedAmount.min(maxByIncome);
        }
        
        return approvedAmount.setScale(2, RoundingMode.HALF_UP);
    }

    private List<EvaluationResponseDTO.EligibleProductDTO> evaluateProducts(
            List<CreditProductEntity> products, CustomerEntity customer, BigDecimal approvedAmount) {
        
        List<EvaluationResponseDTO.EligibleProductDTO> evaluatedProducts = new ArrayList<>();
        
        for (CreditProductEntity product : products) {
            int eligibilityScore = calculateEligibilityScore(product, customer, approvedAmount);
            
            if (eligibilityScore >= 50) { // Minimum threshold for eligibility
                String recommendation = getRecommendation(eligibilityScore);
                BigDecimal approvedRate = calculateRate(product, customer);
                List<String> conditions = generateConditions(product, customer);
                
                EvaluationResponseDTO.EligibleProductDTO evaluatedProduct = 
                        EvaluationResponseDTO.EligibleProductDTO.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .approvedAmount(approvedAmount.min(product.getMaximumAmount()))
                                .approvedRate(approvedRate)
                                .eligibilityScore(eligibilityScore)
                                .recommendation(recommendation)
                                .conditions(conditions)
                                .build();
                
                evaluatedProducts.add(evaluatedProduct);
            }
        }
        
        // Sort by eligibility score descending
        evaluatedProducts.sort(Comparator.comparing(
                EvaluationResponseDTO.EligibleProductDTO::getEligibilityScore).reversed());
        
        return evaluatedProducts;
    }

    private int calculateEligibilityScore(CreditProductEntity product, CustomerEntity customer, 
                                        BigDecimal approvedAmount) {
        int score = 0;
        
        // Amount fit score (30%)
        BigDecimal maxAmount = product.getMaximumAmount();
        BigDecimal minAmount = product.getMinimumAmount();
        
        if (approvedAmount.compareTo(minAmount) >= 0 && approvedAmount.compareTo(maxAmount) <= 0) {
            score += 30;
        } else if (approvedAmount.compareTo(maxAmount) > 0) {
            score += 20; // Amount too high
        } else {
            score += 10; // Amount too low
        }
        
        // Customer profile fit score (70%)
        switch (customer.getRiskLevel()) {
            case BAJO -> score += 70;
            case MEDIO -> score += 50;
            case ALTO -> score += 20;
        }
        
        return Math.min(score, 100);
    }

    private String getRecommendation(int eligibilityScore) {
        if (eligibilityScore >= 85) return "Altamente recomendado";
        else if (eligibilityScore >= 70) return "Recomendado";
        else return "No recomendado";
    }

    private BigDecimal calculateRate(CreditProductEntity product, CustomerEntity customer) {
        BigDecimal baseRate = product.getMinimumRate();
        BigDecimal maxRate = product.getMaximumRate();
        BigDecimal rateRange = maxRate.subtract(baseRate);
        
        BigDecimal rateAdjustment = switch (customer.getRiskLevel()) {
            case BAJO -> rateRange.multiply(BigDecimal.valueOf(0.2));
            case MEDIO -> rateRange.multiply(BigDecimal.valueOf(0.5));
            case ALTO -> rateRange.multiply(BigDecimal.valueOf(0.8));
        };
        
        return baseRate.add(rateAdjustment).setScale(2, RoundingMode.HALF_UP);
    }

    private List<String> generateConditions(CreditProductEntity product, CustomerEntity customer) {
        List<String> conditions = new ArrayList<>();
        
        switch (customer.getRiskLevel()) {
            case ALTO -> {
                conditions.add("Garantía adicional requerida");
                conditions.add("Evaluación mensual de capacidad de pago");
                conditions.add("Seguro de desgravamen obligatorio");
            }
            case MEDIO -> {
                conditions.add("Débito automático obligatorio");
                conditions.add("Seguro de desgravamen incluido");
            }
            case BAJO -> {
                conditions.add("Sin garantía adicional requerida");
                conditions.add("Condiciones preferenciales");
            }
        }
        
        return conditions;
    }

    private EvaluationResponseDTO.ClientProfileDTO buildClientProfile(
            CustomerEntity customer, BigDecimal approvedAmount) {
        
        return EvaluationResponseDTO.ClientProfileDTO.builder()
                .identityDocument(customer.getIdentityDocument())
                .creditScore(customer.getCreditScore())
                .riskLevel(customer.getRiskLevel().name().toLowerCase())
                .approvedAmount(approvedAmount)
                .recommendedTerm("12 meses")
                .build();
    }

    private EvaluationResponseDTO.EvaluationSummaryDTO buildSummary(
            List<EvaluationResponseDTO.EligibleProductDTO> eligibleProducts, 
            LocalDateTime evaluationDate) {
        
        EvaluationResponseDTO.BestOptionDTO bestOption = null;
        
        if (!eligibleProducts.isEmpty()) {
            EvaluationResponseDTO.EligibleProductDTO best = eligibleProducts.get(0);
            bestOption = EvaluationResponseDTO.BestOptionDTO.builder()
                    .productId(best.getId())
                    .reason("Mejor tasa de interés y mayor flexibilidad")
                    .build();
        }
        
        return EvaluationResponseDTO.EvaluationSummaryDTO.builder()
                .totalEligibleProducts(eligibleProducts.size())
                .bestOption(bestOption)
                .evaluationDate(evaluationDate)
                .build();
    }
}