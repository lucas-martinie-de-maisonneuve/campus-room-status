package com.CampusRoomStatus.service;

import com.CampusRoomStatus.dto.EventDTO;
import com.CampusRoomStatus.entity.Event;
import com.CampusRoomStatus.entity.Room;
import com.CampusRoomStatus.exception.ApiException;
import com.CampusRoomStatus.exception.ErrorCode;
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
    private final AppMetadataService appMetadataService;
    private final EventMapper eventMapper;

    public EventsService(
            EventsRepository eventsRepository,
            RoomsRepository roomsRepository,
            GoogleCalendarOAuthClient googleCalendarClient,
            AppMetadataService appMetadataService,
            EventMapper eventMapper) {
        this.eventsRepository = eventsRepository;
        this.roomsRepository = roomsRepository;
        this.googleCalendarClient = googleCalendarClient;
        this.appMetadataService = appMetadataService;
        this.eventMapper = eventMapper;
    }

    public List<EventDTO> getEventsForRoom(String roomCode, ZonedDateTime start, ZonedDateTime end) {
        if (roomCode == null || roomCode.isBlank()) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "Le code de la salle est requis");
        }

        Room room = roomsRepository.findByCode(roomCode)
                .orElseThrow(() -> new ApiException(ErrorCode.ROOM_NOT_FOUND, "Salle introuvable : " + roomCode));

        String syncKey = "sync_" + roomCode + "_" + start.toLocalDate() + "_" + end.toLocalDate();
        try {
            if (!appMetadataService.isSynced(syncKey)) {
                syncForPeriod(room, start, end);
                appMetadataService.markSynced(syncKey);
            }

            return eventsRepository
                    .findByRoomIdAndStartTimeBetweenOrderByStartTimeAsc(room.getId(), start, end)
                    .stream()
                    .map(eventMapper::toDTO)
                    .toList();
        } catch (Exception e) {
            throw new ApiException(ErrorCode.DATABASE_ERROR, "Erreur lors de la récupération des événements de la salle");
        }
    }

    @SuppressWarnings("unchecked")
    public void syncForPeriod(Room room, ZonedDateTime start, ZonedDateTime end) {
        if (room.getResourceEmail() == null) {
            return; // pas de mail → pas de sync
        }

        Map<String, Object> response;
        try {
            response = googleCalendarClient.listEvents(room.getResourceEmail(), start, end);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.GOOGLE_SERVICE_UNAVAILABLE,
                    "Impossible de synchroniser les événements pour la salle " + room.getCode());
        }

        if (response == null || !response.containsKey("items")) {
            return;
        }

        List<Map<String, Object>> googleEvents;
        try {
            googleEvents = (List<Map<String, Object>>) response.get("items");
        } catch (ClassCastException e) {
            throw new ApiException(ErrorCode.GOOGLE_SERVICE_UNAVAILABLE,
                    "Format de réponse inattendu lors de la récupération des événements pour " + room.getCode());
        }

        for (Map<String, Object> googleEvent : googleEvents) {
            try {
                saveOrUpdateEvent(room, googleEvent);
            } catch (Exception e) {
                // Log l'erreur pour continuer avec les autres événements
                System.err.println("Erreur lors de la synchronisation d'un événement : " + e.getMessage());
            }
        }
    }

    private void saveOrUpdateEvent(Room room, Map<String, Object> googleEvent) {
        String googleId = (String) googleEvent.get("id");
        if (googleId == null) return;

        Event event = eventsRepository.findByEventGoogleId(googleId).orElse(new Event());
        event.setEventGoogleId(googleId);
        event.setRoom(room);
        event.setTitle((String) googleEvent.get("summary"));
        event.setStatus((String) googleEvent.get("status"));
        event.setLastSyncedAt(ZonedDateTime.now());

        @SuppressWarnings("unchecked")
        Map<String, Object> creator = (Map<String, Object>) googleEvent.get("creator");
        if (creator != null) {
            event.setOrganizerEmail((String) creator.get("email"));
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> startMap = (Map<String, Object>) googleEvent.get("start");

        @SuppressWarnings("unchecked")
        Map<String, Object> endMap = (Map<String, Object>) googleEvent.get("end");

        if (startMap != null && startMap.get("dateTime") != null) {
            event.setStartTime(ZonedDateTime.parse((String) startMap.get("dateTime")));
        }
        if (endMap != null && endMap.get("dateTime") != null) {
            event.setEndTime(ZonedDateTime.parse((String) endMap.get("dateTime")));
        }

        eventsRepository.save(event);
    }

    public Optional<EventDTO> getCurrentEvent(Room room) {
        if (room == null) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "La salle est requise pour récupérer l'événement courant");
        }
        ZonedDateTime now = ZonedDateTime.now();
        try {
            return eventsRepository
                    .findByRoomIdAndStartTimeBetween(room.getId(), now.minusHours(12), now)
                    .stream()
                    .filter(e -> e.getStartTime().isBefore(now) && e.getEndTime().isAfter(now))
                    .findFirst()
                    .map(eventMapper::toDTO);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.DATABASE_ERROR, "Erreur lors de la récupération de l'événement courant");
        }
    }

    public Optional<EventDTO> getNextEvent(Room room) {
        if (room == null) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "La salle est requise pour récupérer l'événement suivant");
        }
        ZonedDateTime now = ZonedDateTime.now();
        try {
            return eventsRepository
                    .findByRoomIdAndStartTimeBetweenOrderByStartTimeAsc(room.getId(), now, now.plusHours(24))
                    .stream()
                    .findFirst()
                    .map(eventMapper::toDTO);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.DATABASE_ERROR, "Erreur lors de la récupération de l'événement suivant");
        }
    }

    @SuppressWarnings("unchecked")
    public void syncFromGoogle(Room room) {
        if (room.getResourceEmail() == null) return;

        Map<String, Object> response;
        try {
            response = googleCalendarClient.listEventsThisWeek(room.getResourceEmail());
        } catch (Exception e) {
            throw new ApiException(ErrorCode.GOOGLE_SERVICE_UNAVAILABLE,
                    "Impossible de synchroniser les événements cette semaine pour la salle " + room.getCode());
        }

        if (response == null || !response.containsKey("items")) {
            return;
        }

        List<Map<String, Object>> googleEvents;
        try {
            googleEvents = (List<Map<String, Object>>) response.get("items");
        } catch (ClassCastException e) {
            throw new ApiException(ErrorCode.GOOGLE_SERVICE_UNAVAILABLE,
                    "Format de réponse inattendu pour les événements cette semaine de la salle " + room.getCode());
        }

        for (Map<String, Object> googleEvent : googleEvents) {
            try {
                saveOrUpdateEvent(room, googleEvent);
            } catch (Exception e) {
                System.err.println("Erreur lors de la synchronisation d'un événement : " + e.getMessage());
            }
        }
    }

    public void syncAllRooms() {
        List<Room> rooms;
        try {
            rooms = roomsRepository.findAll();
        } catch (Exception e) {
            throw new ApiException(ErrorCode.DATABASE_ERROR, "Impossible de récupérer les salles pour synchronisation des événements");
        }

        for (Room room : rooms) {
            try {
                syncFromGoogle(room);
            } catch (Exception e) {
                System.err.println("Erreur lors de la synchronisation des événements pour la salle " + room.getCode() + " : " + e.getMessage());
            }
        }
    }
}