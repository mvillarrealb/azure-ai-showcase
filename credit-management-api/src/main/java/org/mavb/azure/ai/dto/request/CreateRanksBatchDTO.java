package org.mavb.azure.ai.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating multiple ranks in batch.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRanksBatchDTO {

    @NotEmpty(message = "La lista de rangos no puede estar vacía")
    @Size(min = 1, max = 50, message = "El lote debe contener entre 1 y 50 rangos")
    @Valid
    private List<CreateRankDTO> ranks;
}