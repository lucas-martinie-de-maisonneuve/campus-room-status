package com.CampusRoomStatus.controller;

import com.CampusRoomStatus.dto.BuildingDTO;
import com.CampusRoomStatus.service.BuildingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST pour gérer les bâtiments du campus.
 * Fournit des endpoints pour récupérer la liste des bâtiments et les détails
 * d'un bâtiment spécifique.
 * Les données des bâtiments sont synchronisées depuis l'API Google Directory
 * via le service BuildingsService.
 */
@Tag(name = "Buildings", description = "Endpoints pour gérer les bâtiments du campus")
@RestController
@RequestMapping("/buildings")
public class BuildingsController {

    private final BuildingsService buildingsService;

    public BuildingsController(BuildingsService buildingsService) {
        this.buildingsService = buildingsService;
    }

    /**
     * Endpoint pour récupérer la liste de tous les bâtiments.
     * La réponse inclut un timestamp et une liste de BuildingDTO.
     *
     * @return
     */
    @Operation(summary = "Récupérer la liste de tous les bâtiments")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<BuildingDTO> buildings = buildingsService.getAll();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("buildings", buildings);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour récupérer les détails d'un bâtiment spécifique en utilisant son
     * ID Google.
     * Si le bâtiment n'est pas trouvé, retourne une réponse 404 Not Found.
     * @param buildingId
     * @return
     */
    @Operation(summary = "Récupérer les détails d'un bâtiment par son buildingId")
    @GetMapping("/{buildingId}")
    public ResponseEntity<BuildingDTO> getBuildingById(@PathVariable String buildingId) {
        return buildingsService.getByGoogleId(buildingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}