package org.mavb.azure.ai.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.dto.projection.CustomerEmploymentData;
import org.mavb.azure.ai.dto.projection.CustomerEmploymentProjection;
import org.mavb.azure.ai.dto.request.EvaluationRequestDTO;
import org.mavb.azure.ai.dto.response.EvaluationResponseDTO;
import org.mavb.azure.ai.entity.ProductDocument;
import org.mavb.azure.ai.entity.RankDocument;
import org.mavb.azure.ai.exception.EvaluationException;
import org.mavb.azure.ai.repository.CustomerRepository;
import org.mavb.azure.ai.service.AISearchClient;
import org.mavb.azure.ai.service.EvaluationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI Search-based implementation of EvaluationService.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AISearchEvaluationServiceImpl implements EvaluationService {
    
    private final CustomerRepository customerRepository;
    private final AISearchClient aiSearchClient;

    @Override
    @Transactional
    public Mono<EvaluationResponseDTO> evaluateClientEligibility(EvaluationRequestDTO request) {
        log.info("Starting AI Search-based credit evaluation for customer: {}, amount: {}", 
                request.getIdentityDocument(), request.getRequestedAmount());

        return Mono.fromCallable(() -> getCustomerEmploymentProjection(request.getIdentityDocument()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(customerData -> {
                    // Generate semantic description
                    String semanticDescription = customerData.generateSemanticDescription();
                    log.info("Generated semantic description: {}", semanticDescription);
                    
                    // Resolve rank reactively
                    return aiSearchClient.resolveRankReactive(semanticDescription)
                            .flatMap(resolvedRank -> {
                                String rankId = resolvedRank != null ? resolvedRank.getId() : "UNDEFINED";
                                
                                // Search products reactively
                                return aiSearchClient.searchProductsByRankAndAmountReactive(rankId, request.getRequestedAmount())
                                        .map(productResults -> buildEvaluationResponse(request, customerData, resolvedRank, productResults));
                            });
                })
                .onErrorMap(e -> new EvaluationException("Error processing AI Search evaluation: " + e.getMessage(), e));
    }

    /**
     * Gets customer employment data and builds projection.
     */
    private CustomerEmploymentProjection getCustomerEmploymentProjection(String identityDocument) {
        log.debug("Fetching customer employment data for: {}", identityDocument);
        
        List<CustomerEmploymentData> rawData = customerRepository.findCustomerEmploymentDataForSemanticAnalysis(identityDocument);
        
        if (rawData.isEmpty()) {
            throw new EvaluationException("Customer not found: " + identityDocument);
        }
        
        // Build projection from JPA interface projection results
        CustomerEmploymentProjection projection = null;
        
        for (CustomerEmploymentData data : rawData) {
            // Initialize projection with customer data (first row)
            if (projection == null) {
                projection = new CustomerEmploymentProjection(
                    data.getIdentityDocument(), 
                    data.getMonthlyIncome(), 
                    data.getCurrentDebt()
                );
            }
            
            // Add employment record if exists
            if (data.getStartDate() != null) {
                projection.addEmploymentRecord(
                    data.getStartDate(), 
                    data.getEndDate(), 
                    data.getIncome()
                );
            }
        }
        
        log.debug("Built customer projection with {} employment records", 
                projection.getEmploymentHistory().size());
        
        return projection;
    }

    /**
     * Builds evaluation response.
     */
    private EvaluationResponseDTO buildEvaluationResponse(
            EvaluationRequestDTO request,
            CustomerEmploymentProjection customerData,
            RankDocument resolvedRank,
            List<AISearchClient.ProductSearchResult> productResults) {
        
        log.debug("Building evaluation response");
        
        // Build client profile with semantic analysis
        EvaluationResponseDTO.ClientProfileDTO clientProfile = buildClientProfile(
                customerData, resolvedRank, request.getRequestedAmount());
        
        // Convert product search results to eligible products
        List<EvaluationResponseDTO.EligibleProductDTO> eligibleProducts = productResults.stream()
                .map(result -> mapToEligibleProduct(result, request.getRequestedAmount()))
                .sorted(Comparator.comparing(EvaluationResponseDTO.EligibleProductDTO::getEligibilityScore).reversed())
                .collect(Collectors.toList());
        
        // Build summary with best option
        EvaluationResponseDTO.EvaluationSummaryDTO summary = buildEvaluationSummary(eligibleProducts);
        
        return EvaluationResponseDTO.builder()
                .clientProfile(clientProfile)
                .eligibleProducts(eligibleProducts)
                .summary(summary)
                .build();
    }

    /**
     * Builds client profile with semantic rank information.
     */
    private EvaluationResponseDTO.ClientProfileDTO buildClientProfile(
            CustomerEmploymentProjection customerData,
            RankDocument resolvedRank,
            BigDecimal requestedAmount) {
        
        // Calculate basic credit score based on semantic rank
        int creditScore = calculateSemanticCreditScore(resolvedRank, customerData);
        
        // Determine risk level based on rank
        String riskLevel = determineRiskLevel(resolvedRank);
        
        // Calculate approved amount (80% of requested for good profiles)
        BigDecimal approvedAmount = requestedAmount.multiply(
                resolvedRank != null && !"BRONCE".equals(resolvedRank.getId()) ? 
                new BigDecimal("0.8") : new BigDecimal("0.6"));
        
        return EvaluationResponseDTO.ClientProfileDTO.builder()
                .identityDocument(customerData.getIdentityDocument())
                .creditScore(creditScore)
                .riskLevel(riskLevel)
                .approvedAmount(approvedAmount)
                .recommendedTerm("12 meses") // Default term
                .semanticRank(resolvedRank != null ? resolvedRank.getId() : "UNDEFINED")
                .semanticConfidence(resolvedRank != null ? 0.85 : 0.5) // High confidence if rank resolved
                .semanticDescription(customerData.generateSemanticDescription())
                .build();
    }

    /**
     * Maps AI Search product result to eligible product DTO.
     */
    private EvaluationResponseDTO.EligibleProductDTO mapToEligibleProduct(
            AISearchClient.ProductSearchResult searchResult, 
            BigDecimal requestedAmount) {
        
        ProductDocument product = searchResult.getProduct();
        int eligibilityScore = (int) (searchResult.getRelevanceScore() * 100);
        
        return EvaluationResponseDTO.EligibleProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .approvedAmount(requestedAmount)
                .approvedRate(product.getMinimumRate())
                .eligibilityScore(eligibilityScore)
                .recommendation(String.format("Producto recomendado por AI Search (relevancia: %.2f)", 
                               searchResult.getRelevanceScore()))
                .conditions(product.getRequirements() != null ? product.getRequirements() : List.of())
                .build();
    }

    /**
     * Builds evaluation summary with best product option.
     */
    private EvaluationResponseDTO.EvaluationSummaryDTO buildEvaluationSummary(
            List<EvaluationResponseDTO.EligibleProductDTO> eligibleProducts) {
        
        EvaluationResponseDTO.BestOptionDTO bestOption = null;
        
        if (!eligibleProducts.isEmpty()) {
            EvaluationResponseDTO.EligibleProductDTO best = eligibleProducts.get(0);
            bestOption = EvaluationResponseDTO.BestOptionDTO.builder()
                    .productId(best.getId())
                    .reason(String.format("Mejor relevancia semántica (score: %d)", best.getEligibilityScore()))
                    .build();
        }
        
        return EvaluationResponseDTO.EvaluationSummaryDTO.builder()
                .totalEligibleProducts(eligibleProducts.size())
                .bestOption(bestOption)
                .evaluationDate(LocalDateTime.now())
                .build();
    }

    /**
     * Calculates credit score based on semantic rank.
     */
    private int calculateSemanticCreditScore(RankDocument rank, CustomerEmploymentProjection customerData) {
        if (rank == null) {
            return 600; // Default score for undefined rank
        }
        
        // Base scores by rank
        return switch (rank.getId().toUpperCase()) {
            case "PREMIUM" -> 800;
            case "PLATINO" -> 750;
            case "ORO" -> 700;
            case "PLATA" -> 650;
            case "BRONCE" -> 600;
            default -> 600;
        };
    }

    /**
     * Determines risk level based on resolved rank.
     */
    private String determineRiskLevel(RankDocument rank) {
        if (rank == null) {
            return "Medio";
        }
        
        return switch (rank.getId().toUpperCase()) {
            case "PREMIUM", "PLATINO" -> "Bajo";
            case "ORO", "PLATA" -> "Medio";
            case "BRONCE" -> "Alto";
            default -> "Medio";
        };
    }
}