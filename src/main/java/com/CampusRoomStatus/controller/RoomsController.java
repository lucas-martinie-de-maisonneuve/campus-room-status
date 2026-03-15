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

    @Operation(summary = "Récupérer une salle par code avec status et events")
    @GetMapping("/{code}")
    public ResponseEntity<Map<String, Object>> getByCode(@PathVariable String code) {
        return roomsService.getByCode(code)
                .map(roomDTO -> {
                    Room room = roomsRepository.findByCode(code).get();

                    Optional<EventDTO> currentEvent = eventsService.getCurrentEvent(room);
                    Optional<EventDTO> nextEvent = eventsService.getNextEvent(room);

                    ZonedDateTime now = ZonedDateTime.now();
                    ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(now.getZone());
                    ZonedDateTime endOfDay = startOfDay.plusDays(1);
                    List<EventDTO> scheduleToday = eventsService.getEventsForRoom(code, startOfDay, endOfDay);

                    String status = currentEvent.isPresent() ? "occupied" : "available";

                    Map<String, Object> roomMap = new LinkedHashMap<>();
                    roomMap.put("code", roomDTO.getCode());
                    roomMap.put("name", roomDTO.getName());
                    roomMap.put("capacity", roomDTO.getCapacity());
                    roomMap.put("type", roomDTO.getResourceType());
                    roomMap.put("status", status);
                    roomMap.put("current_event", currentEvent.orElse(null));
                    roomMap.put("next_event", nextEvent.orElse(null));
                    roomMap.put("schedule_today", scheduleToday);

                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("timestamp", Instant.now().toString());
                    response.put("room", roomMap);

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}