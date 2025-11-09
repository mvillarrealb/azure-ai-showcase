package org.mavb.azure.ai.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.dto.request.EvaluationRequestDTO;
import org.mavb.azure.ai.dto.response.EvaluationResponseDTO;
import org.mavb.azure.ai.entity.*;
import org.mavb.azure.ai.exception.EvaluationException;
import org.mavb.azure.ai.repository.CreditProductRepository;
import org.mavb.azure.ai.repository.CustomerRepository;
import org.mavb.azure.ai.service.EvaluationService;
import org.mavb.azure.ai.service.ProductSearchService;
import org.mavb.azure.ai.service.impl.RankService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of EvaluationService interface.
 * Provides comprehensive credit evaluation logic including semantic risk assessment and product matching.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EvaluationServiceImpl implements EvaluationService {
    
    private final CustomerRepository customerRepository;
    private final CreditProductRepository creditProductRepository;
    private final RankService rankService;
    private final ProductSearchService productSearchService;

    @Override
    @Transactional
    public EvaluationResponseDTO evaluateClientEligibility(EvaluationRequestDTO request) {
        log.info("Starting credit evaluation for customer: {}, amount: {}", 
                request.getIdentityDocument(), request.getRequestedAmount());

        try {
            // 1. Find customer with employment history
            CustomerEntity customer = findCustomerWithHistory(request.getIdentityDocument());
            
            // 2. Generate semantic profile and classify rank
            SemanticRankResult semanticRankResult = classifyCustomerRank(customer);
            
            // 3. Calculate basic credit metrics
            CreditMetrics creditMetrics = calculateCreditMetrics(customer, request.getRequestedAmount());
            
            // 4. Find eligible products using traditional logic
            List<CreditProductEntity> traditionalProducts = findTraditionalEligibleProducts(
                    request.getRequestedAmount(), "S/", null);
            
            // 5. Find products using semantic search
            List<ProductSearchService.ProductSearchResult> semanticProducts = findSemanticEligibleProducts(
                    semanticRankResult.getRank(), request.getRequestedAmount(), customer);
            
            // 6. Combine and rank all products
            List<EvaluationResponseDTO.EligibleProductDTO> combinedProducts = 
                    combineAndRankProducts(traditionalProducts, semanticProducts, creditMetrics);
            
            // 7. Build response
            return buildEvaluationResponse(
                    customer, request, semanticRankResult, creditMetrics, combinedProducts);
                    
        } catch (Exception e) {
            log.error("Error evaluating credit eligibility for customer {}: {}", 
                    request.getIdentityDocument(), e.getMessage(), e);
            throw new EvaluationException("Error processing credit evaluation: " + e.getMessage(), e);
        }
    }

    /**
     * Finds customer with employment history from database.
     */
    private CustomerEntity findCustomerWithHistory(String identityDocument) {
        Optional<CustomerEntity> customerOpt = customerRepository
                .findByIdentityDocumentWithRecentEmployments(identityDocument);
        
        if (customerOpt.isEmpty()) {
            throw new EvaluationException("Customer not found: " + identityDocument);
        }
        
        CustomerEntity customer = customerOpt.get();
        log.debug("Found customer {} with {} employment records", 
                customer.getIdentityDocument(), customer.getEmploymentHistory().size());
        
        return customer;
    }

    /**
     * Classifies customer rank using semantic analysis of employment history.
     */
    private SemanticRankResult classifyCustomerRank(CustomerEntity customer) {
        log.debug("Classifying customer rank for: {}", customer.getIdentityDocument());
        
        String semanticDescription = generateSemanticProfile(customer);
        log.debug("Generated semantic profile: {}", semanticDescription);
        
        RankDocument rankDocument = rankService.resolveRank(semanticDescription);
        
        if (rankDocument == null) {
            log.warn("No rank classification found for customer {}, defaulting to BRONCE", 
                    customer.getIdentityDocument());
            return new SemanticRankResult("BRONCE", 0.5, semanticDescription);
        }
        
        double confidence = 0.85;
        
        log.info("Customer {} classified as rank {} with confidence {}", 
                customer.getIdentityDocument(), rankDocument.getName(), confidence);
        
        return new SemanticRankResult(rankDocument.getName(), confidence, semanticDescription);
    }

    /**
     * Generates semantic profile text from customer and employment data.
     */
    private String generateSemanticProfile(CustomerEntity customer) {
        StringBuilder profile = new StringBuilder();
        
        profile.append("Cliente con ingresos mensuales de S/")
                .append(customer.getMonthlyIncome() != null ? customer.getMonthlyIncome() : 0);
        
        List<EmploymentHistoryEntity> employmentHistory = customer.getEmploymentHistory();
        if (!employmentHistory.isEmpty()) {
            EmploymentHistoryEntity currentJob = customer.getCurrentEmployment();
            if (currentJob != null) {
                profile.append(", actualmente trabaja como ")
                        .append(currentJob.getPosition())
                        .append(" en ")
                        .append(currentJob.getCompanyName())
                        .append(" desde ")
                        .append(currentJob.getStartDate());
                
                long monthsInCurrentJob = currentJob.getEmploymentDurationInMonths();
                profile.append(" (").append(monthsInCurrentJob).append(" meses de experiencia)");
            }
            
            long totalExperienceMonths = employmentHistory.stream()
                    .mapToLong(EmploymentHistoryEntity::getEmploymentDurationInMonths)
                    .sum();
            
            profile.append(". Experiencia laboral total: ")
                    .append(totalExperienceMonths)
                    .append(" meses");
        }
        
        if (customer.getCurrentDebt() != null && customer.getCurrentDebt().compareTo(BigDecimal.ZERO) > 0) {
            profile.append(". Deuda actual: S/").append(customer.getCurrentDebt());
        }
        
        return profile.toString();
    }

    /**
     * Calculates basic credit metrics for the customer.
     */
    private CreditMetrics calculateCreditMetrics(CustomerEntity customer, BigDecimal requestedAmount) {
        BigDecimal monthlyIncome = customer.getMonthlyIncome() != null ? customer.getMonthlyIncome() : BigDecimal.ZERO;
        BigDecimal currentDebt = customer.getCurrentDebt() != null ? customer.getCurrentDebt() : BigDecimal.ZERO;
        
        double debtToIncomeRatio = monthlyIncome.compareTo(BigDecimal.ZERO) > 0 ? 
                currentDebt.divide(monthlyIncome, 4, RoundingMode.HALF_UP).doubleValue() : 1.0;
        
        int creditScore = calculateBasicCreditScore(customer, debtToIncomeRatio);
        
        BigDecimal approvedAmount = debtToIncomeRatio < 0.4 && creditScore > 600 ?
                requestedAmount.multiply(new BigDecimal("0.8")) : 
                requestedAmount.multiply(new BigDecimal("0.6"));
        
        return new CreditMetrics(creditScore, debtToIncomeRatio, approvedAmount);
    }

    /**
     * Calculates basic credit score based on customer data.
     */
    private int calculateBasicCreditScore(CustomerEntity customer, double debtToIncomeRatio) {
        int baseScore = 650;
        
        // Adjust based on income
        BigDecimal monthlyIncome = customer.getMonthlyIncome() != null ? customer.getMonthlyIncome() : BigDecimal.ZERO;
        if (monthlyIncome.compareTo(new BigDecimal("5000")) > 0) {
            baseScore += 50;
        }
        if (monthlyIncome.compareTo(new BigDecimal("10000")) > 0) {
            baseScore += 50;
        }
        
        // Adjust based on debt-to-income ratio
        if (debtToIncomeRatio < 0.2) {
            baseScore += 100;
        } else if (debtToIncomeRatio > 0.5) {
            baseScore -= 100;
        }
        
        // Adjust based on employment stability
        EmploymentHistoryEntity currentJob = customer.getCurrentEmployment();
        if (currentJob != null) {
            long monthsInJob = currentJob.getEmploymentDurationInMonths();
            if (monthsInJob > 24) {
                baseScore += 50;
            } else if (monthsInJob < 6) {
                baseScore -= 50;
            }
        }
        
        return Math.max(300, Math.min(850, baseScore));
    }

    /**
     * Finds eligible products using traditional repository logic.
     */
    private List<CreditProductEntity> findTraditionalEligibleProducts(BigDecimal amount, String currency, String category) {
        return creditProductRepository.findEligibleProducts(amount, currency, category);
    }

    /**
     * Finds eligible products using semantic search in AI Search.
     */
    private List<ProductSearchService.ProductSearchResult> findSemanticEligibleProducts(
            String customerRank, BigDecimal requestedAmount, CustomerEntity customer) {
        
        // Generate customer needs description for semantic matching
        String customerNeeds = generateCustomerNeedsDescription(customer, requestedAmount);
        
        return productSearchService.searchByRankAndNeeds(customerRank, requestedAmount, customerNeeds);
    }

    /**
     * Generates description of customer needs for semantic product matching.
     */
    private String generateCustomerNeedsDescription(CustomerEntity customer, BigDecimal requestedAmount) {
        StringBuilder needs = new StringBuilder();
        
        needs.append("Cliente busca crédito por S/").append(requestedAmount);
        
        // Infer purpose based on amount
        if (requestedAmount.compareTo(new BigDecimal("20000")) > 0) {
            needs.append(" para inversión o proyecto importante");
        } else if (requestedAmount.compareTo(new BigDecimal("5000")) > 0) {
            needs.append(" para gastos personales o mejoras");
        } else {
            needs.append(" para gastos menores o emergencias");
        }
        
        // Add employment context
        EmploymentHistoryEntity currentJob = customer.getCurrentEmployment();
        if (currentJob != null) {
            needs.append(", trabaja como ").append(currentJob.getPosition());
        }
        
        return needs.toString();
    }

    /**
     * Combines traditional and semantic product results and ranks them.
     */
    private List<EvaluationResponseDTO.EligibleProductDTO> combineAndRankProducts(
            List<CreditProductEntity> traditionalProducts,
            List<ProductSearchService.ProductSearchResult> semanticProducts,
            CreditMetrics creditMetrics) {
        
        List<EvaluationResponseDTO.EligibleProductDTO> combinedProducts = new ArrayList<>();
        
        // Add traditional products
        traditionalProducts.forEach(product -> {
            EvaluationResponseDTO.EligibleProductDTO dto = mapTraditionalProduct(product, creditMetrics);
            combinedProducts.add(dto);
        });
        
        // Add semantic products (avoid duplicates)
        semanticProducts.forEach(searchResult -> {
            ProductDocument product = searchResult.getProduct();
            boolean isDuplicate = combinedProducts.stream()
                    .anyMatch(existing -> existing.getId().equals(product.getId()));
            
            if (!isDuplicate) {
                EvaluationResponseDTO.EligibleProductDTO dto = mapSemanticProduct(searchResult, creditMetrics);
                combinedProducts.add(dto);
            }
        });
        
        // Sort by eligibility score (descending)
        combinedProducts.sort(Comparator.comparing(EvaluationResponseDTO.EligibleProductDTO::getEligibilityScore).reversed());
        
        return combinedProducts;
    }

    /**
     * Maps traditional product to DTO.
     */
    private EvaluationResponseDTO.EligibleProductDTO mapTraditionalProduct(
            CreditProductEntity product, CreditMetrics creditMetrics) {
        
        return EvaluationResponseDTO.EligibleProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .approvedAmount(creditMetrics.getApprovedAmount())
                .approvedRate(product.getMinimumRate())
                .eligibilityScore(75) // Base score for traditional matching
                .recommendation("Producto elegible por criterios tradicionales")
                .conditions(product.getRequirements() != null ? product.getRequirements() : List.of())
                .build();
    }

    /**
     * Maps semantic product to DTO.
     */
    private EvaluationResponseDTO.EligibleProductDTO mapSemanticProduct(
            ProductSearchService.ProductSearchResult searchResult, CreditMetrics creditMetrics) {
        
        ProductDocument product = searchResult.getProduct();
        int eligibilityScore = (int) (searchResult.getRelevanceScore() * 100);
        
        return EvaluationResponseDTO.EligibleProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .approvedAmount(creditMetrics.getApprovedAmount())
                .approvedRate(product.getMinimumRate())
                .eligibilityScore(eligibilityScore)
                .recommendation("Producto recomendado por análisis semántico (score: " + 
                               String.format("%.2f", searchResult.getRelevanceScore()) + ")")
                .conditions(product.getRequirements() != null ? product.getRequirements() : List.of())
                .build();
    }

    /**
     * Builds the final evaluation response.
     */
    private EvaluationResponseDTO buildEvaluationResponse(
            CustomerEntity customer,
            EvaluationRequestDTO request,
            SemanticRankResult semanticRankResult,
            CreditMetrics creditMetrics,
            List<EvaluationResponseDTO.EligibleProductDTO> products) {
        
        // Build client profile
        EvaluationResponseDTO.ClientProfileDTO clientProfile = EvaluationResponseDTO.ClientProfileDTO.builder()
                .identityDocument(customer.getIdentityDocument())
                .creditScore(creditMetrics.getCreditScore())
                .riskLevel(semanticRankResult.getRank())
                .approvedAmount(creditMetrics.getApprovedAmount())
                .recommendedTerm("12 meses") // Simplified
                .semanticRank(semanticRankResult.getRank())
                .semanticConfidence(semanticRankResult.getConfidence())
                .semanticDescription(semanticRankResult.getSemanticDescription())
                .build();
        
        // Find best option
        EvaluationResponseDTO.BestOptionDTO bestOption = null;
        if (!products.isEmpty()) {
            EvaluationResponseDTO.EligibleProductDTO bestProduct = products.get(0);
            bestOption = EvaluationResponseDTO.BestOptionDTO.builder()
                    .productId(bestProduct.getId())
                    .reason("Mejor puntuación de elegibilidad (" + bestProduct.getEligibilityScore() + ")")
                    .build();
        }
        
        // Build summary
        EvaluationResponseDTO.EvaluationSummaryDTO summary = EvaluationResponseDTO.EvaluationSummaryDTO.builder()
                .totalEligibleProducts(products.size())
                .bestOption(bestOption)
                .evaluationDate(LocalDateTime.now())
                .build();
        
        return EvaluationResponseDTO.builder()
                .clientProfile(clientProfile)
                .eligibleProducts(products)
                .summary(summary)
                .build();
    }

    // Inner classes for data transfer
    private static class SemanticRankResult {
        private final String rank;
        private final double confidence;
        private final String semanticDescription;

        public SemanticRankResult(String rank, double confidence, String semanticDescription) {
            this.rank = rank;
            this.confidence = confidence;
            this.semanticDescription = semanticDescription;
        }

        public String getRank() { return rank; }
        public double getConfidence() { return confidence; }
        public String getSemanticDescription() { return semanticDescription; }
    }

    private static class CreditMetrics {
        private final int creditScore;
        private final double debtToIncomeRatio;
        private final BigDecimal approvedAmount;

        public CreditMetrics(int creditScore, double debtToIncomeRatio, BigDecimal approvedAmount) {
            this.creditScore = creditScore;
            this.debtToIncomeRatio = debtToIncomeRatio;
            this.approvedAmount = approvedAmount;
        }

        public int getCreditScore() { return creditScore; }
        public double getDebtToIncomeRatio() { return debtToIncomeRatio; }
        public BigDecimal getApprovedAmount() { return approvedAmount; }
    }
}