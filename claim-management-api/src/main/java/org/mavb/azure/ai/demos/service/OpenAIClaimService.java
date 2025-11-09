package org.mavb.azure.ai.demos.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.demos.config.OpenAIConfig;
import org.mavb.azure.ai.demos.dto.request.ClaimImportReason;
import org.mavb.azure.ai.demos.dto.request.ImportClaimDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OpenAIClaimService {
    private final OpenAIConfig openAIConfig;
    private OpenAIClient openAIClient;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void postConstruct() {
        this.openAIClient = openAIConfig.createClient();
    }

    /**
     * Versión reactiva del método que maneja la llamada a OpenAI de forma no bloqueante.
     */
    public Mono<List<ImportClaimDto>> getReasonDataFromModelReactive(List<ImportClaimDto> claims) {
        return evaluateReactive(claims)
                .map(claimReasons -> claims.parallelStream()
                        .map(it -> checkReason(it, claimReasons))
                        .toList());
    }

    private Mono<List<ClaimImportReason>> evaluateReactive(List<ImportClaimDto> claims) {
        return Mono.fromCallable(() -> {
            StringBuilder prompt = new StringBuilder(openAIConfig.getSystemPrompt());
            for (ImportClaimDto claim : claims) {
                prompt.append(String.format("RowNumber: %s - Description: %s \n", claim.getRowNumber(), claim.getDescription()));
            }
            log.info("Preparing Prompt Input to model {}", prompt.toString());
            var chatCompletions = openAIClient.getChatCompletions(
                    openAIConfig.getDeploymentName(),
                    new ChatCompletionsOptions(List.of(new ChatRequestUserMessage(prompt.toString())))
            );
            var rawJsonAnswer = chatCompletions.getChoices().getFirst().getMessage().getContent();
            return parseOpenAIResponse(rawJsonAnswer);
        })
        .subscribeOn(Schedulers.boundedElastic()) // Ejecutar en un scheduler que soporta operaciones bloqueantes
        .doOnError(error -> log.error("Error en llamada reactiva a OpenAI: {}", error.getMessage(), error))
        .onErrorReturn(Collections.emptyList()); // Retornar lista vacía en caso de error
    }

    private List<ClaimImportReason> parseOpenAIResponse(String rawJsonAnswer) {
        log.debug("Raw JSON response from OpenAI: {}", rawJsonAnswer);
        try {
            List<ClaimImportReason> claimReasons = objectMapper.readValue(
                    rawJsonAnswer,
                    new TypeReference<List<ClaimImportReason>>() {}
            );
            log.info("Successfully deserialized {} claim reasons from OpenAI response", claimReasons.size());
            return claimReasons;
        } catch (JsonProcessingException e) {
            log.error("Error deserializing OpenAI response. Raw JSON: {}", rawJsonAnswer, e);
            log.error("Deserialization error details - Message: {}, Location: line {}, column {}",
                    e.getOriginalMessage(),
                    e.getLocation() != null ? e.getLocation().getLineNr() : "N/A",
                    e.getLocation() != null ? e.getLocation().getColumnNr() : "N/A"
            );
            return Collections.emptyList();
        }
    }

    private ImportClaimDto checkReason(ImportClaimDto importClaim, List<ClaimImportReason> claimReasons) {
        Optional<ClaimImportReason> importReasonOptional = claimReasons
                .stream()
                .filter(it -> Objects.equals(it.getRowNumber(), importClaim.getRowNumber()))
                .findFirst();
        importReasonOptional.ifPresent(reason-> {
            importClaim.setReason(reason.getMainCategory());
            importClaim.setSubReason(reason.getSubCategory());
        });
        return importClaim;
    }
}
