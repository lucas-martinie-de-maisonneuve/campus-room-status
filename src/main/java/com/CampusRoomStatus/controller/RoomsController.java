package com.CampusRoomStatus.controller;

import com.CampusRoomStatus.dto.RoomDTO;
import com.CampusRoomStatus.service.RoomsService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rooms")
public class RoomsController {

    private final RoomsService roomsService;

    public RoomsController(RoomsService roomsService) {
        this.roomsService = roomsService;
    }

    @Operation(summary = "Récupérer la liste de toutes les salles")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<RoomDTO> rooms = roomsService.getAll();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("count", rooms.size());
        response.put("rooms", rooms);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Récupérer la liste des salles d'un bâtiment par son buildingGoogleId")
    @GetMapping("/building/{buildingGoogleId}")
    public ResponseEntity<Map<String, Object>> getByBuilding(@PathVariable String buildingGoogleId) {
        List<RoomDTO> rooms = roomsService.getByBuilding(buildingGoogleId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("count", rooms.size());
        response.put("rooms", rooms);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Récupérer les détails d'une salle par son code")
    @GetMapping("/{code}")
    public ResponseEntity<Map<String, Object>> getByCode(@PathVariable String code) {
        return roomsService.getByCode(code)
                .map(room -> {
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("timestamp", Instant.now().toString());
                    response.put("room", room);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

}