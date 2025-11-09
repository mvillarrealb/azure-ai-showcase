package org.mavb.azure.ai.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mavb.azure.ai.dto.request.CreateRankDTO;
import org.mavb.azure.ai.dto.request.CreateRanksBatchDTO;
import org.mavb.azure.ai.dto.request.RankFilterDTO;
import org.mavb.azure.ai.dto.response.RankDTO;
import org.mavb.azure.ai.dto.response.RankListResponseDTO;
import org.mavb.azure.ai.dto.response.RanksBatchResponseDTO;
import org.mavb.azure.ai.service.RankService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for rank management.
 * Handles HTTP requests for rank listing, creation, and batch creation.
 * All ranks are automatically synchronized to Azure AI Search via JPA listeners.
 * 
 * Base path: /ranks
 * 
 * Endpoints:
 * - GET /ranks - Lista rangos con filtros opcionales y paginación
 * - POST /ranks - Crea un nuevo rango con sincronización automática a AI Search
 * - POST /ranks/batch - Crea múltiples rangos en lote con sincronización automática
 */
@RestController
@RequestMapping("/ranks")
@RequiredArgsConstructor
@Slf4j
public class RankController {

    private final RankService rankService;

    /**
     * Retrieves a paginated list of ranks with optional filtering.
     * 
     * @param name Filtro opcional por nombre del rango
     * @param page Número de página (empezando en 0)
     * @param size Tamaño de página
     * @param sort Campo de ordenación
     * @return ResponseEntity con lista paginada de rangos
     */
    @GetMapping
    public ResponseEntity<RankListResponseDTO> getRanks(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort) {
        
        log.info("📋 Getting ranks - page: {}, size: {}, sort: {}, name filter: '{}'", 
                page, size, sort, name);

        RankFilterDTO filter = RankFilterDTO.builder()
                .name(name)
                .build();

        Sort.Direction direction = Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        RankListResponseDTO response = rankService.getRanks(filter, pageable);

        log.info("✅ Retrieved {} ranks successfully", response.getData().size());
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new rank in the database.
     * The rank will be automatically synchronized to Azure AI Search via RankSyncListener.
     * 
     * @param createRankDTO Datos del rango a crear
     * @return ResponseEntity con detalles del rango creado
     */
    @PostMapping
    public ResponseEntity<RankDTO> createRank(@Valid @RequestBody CreateRankDTO createRankDTO) {
        log.info("🎯 Creating new rank with ID: {} and name: '{}'", 
                createRankDTO.getId(), createRankDTO.getName());
        
        RankDTO createdRank = rankService.createRank(createRankDTO);
        
        log.info("✅ Rank '{}' created successfully with ID: {}", 
                createdRank.getName(), createdRank.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRank);
    }

    /**
     * Creates multiple ranks in batch.
     * All ranks will be automatically synchronized to Azure AI Search via RankSyncListener.
     * 
     * @param createRanksBatchDTO Datos de los rangos a crear en lote
     * @return ResponseEntity con resultado de la creación en lote
     */
    @PostMapping("/batch")
    public ResponseEntity<RanksBatchResponseDTO> createRanksBatch(@Valid @RequestBody CreateRanksBatchDTO createRanksBatchDTO) {
        log.info("🎯 Creating {} ranks in batch", createRanksBatchDTO.getRanks().size());
        
        RanksBatchResponseDTO response = rankService.createRanksBatch(createRanksBatchDTO);
        
        log.info("✅ Batch creation completed: {}/{} ranks created successfully", 
                response.getCreatedRanks(), response.getTotalRanks());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}