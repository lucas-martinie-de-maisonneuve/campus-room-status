package com.CampusRoomStatus.controller;

import com.CampusRoomStatus.dto.EventDTO;
import com.CampusRoomStatus.service.EventsService;
import com.CampusRoomStatus.dto.RoomDTO;
import com.CampusRoomStatus.entity.Room;
import com.CampusRoomStatus.service.RoomsService;
import com.CampusRoomStatus.repository.RoomsRepository;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rooms")
public class RoomsController {

    private final RoomsService roomsService;
    private final RoomsRepository roomsRepository;
    private final EventsService eventsService;

    public RoomsController(RoomsService roomsService, RoomsRepository roomsRepository, EventsService eventsService) {
        this.roomsService = roomsService;
        this.roomsRepository = roomsRepository;
        this.eventsService = eventsService;
    }

    @Operation(summary = "Récupérer la liste de toutes les salles avec filtres optionnels")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false) String building,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer capacity_min,
            @RequestParam(required = false) Integer capacity_max,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order) {

        List<RoomDTO> roomDTOs = roomsService.getFiltered(building, type, capacity_min, capacity_max, sort, order);

        List<Map<String, Object>> rooms = roomDTOs.stream().map(roomDTO -> {
            Room room = roomsRepository.findByCode(roomDTO.getCode()).orElse(null);

            Optional<EventDTO> currentEvent = room != null ? eventsService.getCurrentEvent(room) : Optional.empty();
            Optional<EventDTO> nextEvent = room != null ? eventsService.getNextEvent(room) : Optional.empty();

            String roomStatus = currentEvent.isPresent() ? "occupied" : "available";

            Map<String, Object> roomMap = new LinkedHashMap<>();
            roomMap.put("code", roomDTO.getCode());
            roomMap.put("name", roomDTO.getName());
            if (roomDTO.getBuilding() != null) {
                Map<String, Object> buildingMap = new LinkedHashMap<>();
                buildingMap.put("id", roomDTO.getBuilding().getId());
                buildingMap.put("name", roomDTO.getBuilding().getName());
                roomMap.put("building", buildingMap);
            }
            roomMap.put("floor", roomDTO.getFloor());
            roomMap.put("capacity", roomDTO.getCapacity());
            roomMap.put("type", roomDTO.getResourceType());
            roomMap.put("status", roomStatus);
            roomMap.put("current_event", currentEvent.orElse(null));
            roomMap.put("next_event", nextEvent.orElse(null));

            return roomMap;
        }).toList();

        List<Map<String, Object>> filtered = status != null
                ? rooms.stream().filter(r -> status.equals(r.get("status"))).toList()
                : rooms;

        Map<String, Object> filters = new LinkedHashMap<>();
        if (building != null) filters.put("building", building);
        if (type != null) filters.put("type", type);
        if (status != null) filters.put("status", status);
        if (capacity_min != null) filters.put("capacity_min", capacity_min);
        if (capacity_max != null) filters.put("capacity_max", capacity_max);
        if (sort != null) filters.put("sort", sort);
        if (order != null) filters.put("order", order);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", Instant.now().toString());
        if (!filters.isEmpty()) response.put("filters", filters);
        response.put("count", filtered.size());
        response.put("rooms", filtered);

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

    @Operation(summary = "Récupérer une salle par code avec status et events")
    @GetMapping("/{code}")
    public ResponseEntity<Map<String, Object>> getByCode(@PathVariable String code) {
        Map<String, Object> roomData = roomsService.getRoomWithStatusAndEvents(code);
        return ResponseEntity.ok(roomData);
    }
}