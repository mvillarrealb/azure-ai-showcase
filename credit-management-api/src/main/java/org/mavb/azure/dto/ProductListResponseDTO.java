package org.mavb.azure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for paginated product list responses.
 * Contains the list of products along with pagination metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponseDTO {

    @JsonProperty("data")
    private List<ProductDTO> data;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("totalPages")
    private Integer totalPages;

    @JsonProperty("currentPage")
    private Integer currentPage;
}