package com.CampusRoomStatus.service;

import com.CampusRoomStatus.dto.EventDTO;
import com.CampusRoomStatus.entity.Event;
import com.CampusRoomStatus.entity.Room;
import com.CampusRoomStatus.integration.google.GoogleCalendarOAuthClient;
import com.CampusRoomStatus.mapper.EventMapper;
import com.CampusRoomStatus.repository.EventsRepository;
import com.CampusRoomStatus.repository.RoomsRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EventsService {

    private final EventsRepository eventsRepository;
    private final RoomsRepository roomsRepository;
    private final GoogleCalendarOAuthClient googleCalendarClient;
    private final EventMapper eventMapper;

    public EventsService(
            EventsRepository eventsRepository,
            RoomsRepository roomsRepository,
            GoogleCalendarOAuthClient googleCalendarClient,
            EventMapper eventMapper) {
        this.eventsRepository = eventsRepository;
        this.roomsRepository = roomsRepository;
        this.googleCalendarClient = googleCalendarClient;
        this.eventMapper = eventMapper;
    }

    public List<EventDTO> getEventsForRoom(String roomCode, ZonedDateTime start, ZonedDateTime end) {
        Room room = roomsRepository.findByCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Salle introuvable : " + roomCode));

        return eventsRepository
                .findByRoomIdAndStartTimeBetweenOrderByStartTimeAsc(room.getId(), start, end)
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }

    public Optional<EventDTO> getCurrentEvent(Room room) {
        ZonedDateTime now = ZonedDateTime.now();
        return eventsRepository
                .findByRoomIdAndStartTimeBetween(room.getId(), now.minusHours(12), now)
                .stream()
                .filter(e -> e.getStartTime().isBefore(now) && e.getEndTime().isAfter(now))
                .findFirst()
                .map(eventMapper::toDTO);
    }

    public Optional<EventDTO> getNextEvent(Room room) {
        ZonedDateTime now = ZonedDateTime.now();
        return eventsRepository
                .findByRoomIdAndStartTimeBetweenOrderByStartTimeAsc(room.getId(), now, now.plusHours(24))
                .stream()
                .findFirst()
                .map(eventMapper::toDTO);
    }

    @SuppressWarnings("unchecked")
    public void syncFromGoogle(Room room) {
        if (room.getResourceEmail() == null) {
            return;
        }

        Map<String, Object> response;
        try {
            response = googleCalendarClient.listEventsThisWeek(room.getResourceEmail());
        } catch (Exception e) {
            System.out.println("Impossible de syncer les events pour : " + room.getCode() + " — " + e.getMessage());
            return;
        }

        if (response == null || !response.containsKey("items")) {
            return;
        }

        List<Map<String, Object>> googleEvents = (List<Map<String, Object>>) response.get("items");

        for (Map<String, Object> googleEvent : googleEvents) {
            String googleId = (String) googleEvent.get("id");

            Event event = eventsRepository.findByEventGoogleId(googleId)
                    .orElse(new Event());

            event.setEventGoogleId(googleId);
            event.setRoom(room);
            event.setTitle((String) googleEvent.get("summary"));
            event.setStatus((String) googleEvent.get("status"));
            event.setLastSyncedAt(ZonedDateTime.now());

            Map<String, Object> creator = (Map<String, Object>) googleEvent.get("creator");
            if (creator != null) {
                event.setOrganizerEmail((String) creator.get("email"));
            }

            Map<String, Object> start = (Map<String, Object>) googleEvent.get("start");
            Map<String, Object> end = (Map<String, Object>) googleEvent.get("end");

            if (start != null && start.get("dateTime") != null) {
                event.setStartTime(ZonedDateTime.parse((String) start.get("dateTime")));
            }
            if (end != null && end.get("dateTime") != null) {
                event.setEndTime(ZonedDateTime.parse((String) end.get("dateTime")));
            }

            eventsRepository.save(event);
        }
    }

    public void syncAllRooms() {
        List<Room> rooms = roomsRepository.findAll();
        for (Room room : rooms) {
            syncFromGoogle(room);
        }
    }
}