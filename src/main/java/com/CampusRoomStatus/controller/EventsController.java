package com.CampusRoomStatus.controller;

import com.CampusRoomStatus.dto.EventDTO;
import com.CampusRoomStatus.service.EventsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Events", description = "Gestion des événements")
@RestController
@RequestMapping("/rooms")
public class EventsController {

    private final EventsService eventsService;

    public EventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @Operation(summary = "Récupérer le planning d'une salle sur une période")
    @GetMapping("/{code}/schedule")
    public ResponseEntity<Map<String, Object>> getSchedule(
            @PathVariable String code,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {

        ZonedDateTime startDate;
        ZonedDateTime endDate;

        if (start != null && end != null) {
            startDate = ZonedDateTime.parse(start + "T00:00:00+01:00");
            endDate = ZonedDateTime.parse(end + "T23:59:59+01:00");
        } else {
            ZonedDateTime now = ZonedDateTime.now();
            startDate = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .toLocalDate().atStartOfDay(now.getZone());
            endDate = startDate.plusDays(7);
        }

        List<EventDTO> events = eventsService.getEventsForRoom(code, startDate, endDate);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("room_code", code);

        Map<String, String> period = new LinkedHashMap<>();
        period.put("start", startDate.toLocalDate().toString());
        period.put("end", endDate.toLocalDate().toString());
        response.put("period", period);

        response.put("count", events.size());
        response.put("events", events);

        return ResponseEntity.ok(response);
    }
}