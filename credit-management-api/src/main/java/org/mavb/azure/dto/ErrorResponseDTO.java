package org.mavb.azure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for API error responses.
 * Provides structured error information including field-specific validation errors.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

    @JsonProperty("error")
    private String error;

    @JsonProperty("message")
    private String message;

    @JsonProperty("details")
    private List<ErrorDetailDTO> details;

    /**
     * DTO for specific error details.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetailDTO {

        @JsonProperty("field")
        private String field;

        @JsonProperty("message")
        private String message;
    }
}