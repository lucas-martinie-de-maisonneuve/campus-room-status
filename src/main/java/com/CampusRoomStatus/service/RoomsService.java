package com.CampusRoomStatus.service;

import com.CampusRoomStatus.dto.EventDTO;
import com.CampusRoomStatus.dto.RoomDTO;
import com.CampusRoomStatus.entity.Room;
import com.CampusRoomStatus.exception.ApiException;
import com.CampusRoomStatus.exception.ErrorCode;
import com.CampusRoomStatus.integration.google.GoogleDirectoryOAuthClient;
import com.CampusRoomStatus.mapper.RoomMapper;
import com.CampusRoomStatus.repository.BuildingsRepository;
import com.CampusRoomStatus.repository.RoomsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RoomsService {

    private final RoomsRepository roomsRepository;
    private final BuildingsRepository buildingsRepository;
    private final GoogleDirectoryOAuthClient googleDirectoryOAuthClient;
    private final RoomMapper roomMapper;
     private final EventsService eventsService;

    public RoomsService(
            RoomsRepository roomsRepository,
            BuildingsRepository buildingsRepository,
            GoogleDirectoryOAuthClient googleDirectoryOAuthClient,
            RoomMapper roomMapper,
            EventsService eventsService) {
        this.roomsRepository = roomsRepository;
        this.buildingsRepository = buildingsRepository;
        this.googleDirectoryOAuthClient = googleDirectoryOAuthClient;
        this.roomMapper = roomMapper;
        this.eventsService = eventsService;
    }

    public List<RoomDTO> getAll() {
        try {
            return roomsRepository.findAll()
                    .stream()
                    .map(roomMapper::toDTO)
                    .toList();
        } catch (Exception e) {
            throw new ApiException(ErrorCode.DATABASE_ERROR, "Impossible de récupérer les salles");
        }
    }

    public Optional<RoomDTO> getByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "Le code de la salle est requis");
        }
        try {
            return roomsRepository.findByCode(code)
                    .map(roomMapper::toDTO);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.DATABASE_ERROR, "Erreur lors de la récupération de la salle");
        }
    }

    @SuppressWarnings("unchecked")
    public void syncFromGoogle() {
        Map<String, Object> response;
        try {
            response = googleDirectoryOAuthClient.listRooms();
        } catch (Exception e) {
            throw new ApiException(ErrorCode.GOOGLE_SERVICE_UNAVAILABLE, "Impossible de récupérer les salles depuis Google");
        }

        if (response == null || !response.containsKey("items")) {
            throw new ApiException(ErrorCode.GOOGLE_SERVICE_UNAVAILABLE, "Réponse Google invalide : champ 'items' manquant");
        }

        List<Map<String, Object>> googleRooms;
        try {
            googleRooms = (List<Map<String, Object>>) response.get("items");
        } catch (ClassCastException e) {
            throw new ApiException(ErrorCode.GOOGLE_SERVICE_UNAVAILABLE, "Réponse Google invalide : format inattendu pour 'items'");
        }

        for (Map<String, Object> googleRoom : googleRooms) {
            try {
                String googleId = String.valueOf(googleRoom.get("resourceId"));
                Room room = roomsRepository.findByRoomGoogleId(googleId).orElse(new Room());

                String resourceName = (String) googleRoom.get("resourceName");
                String floor = (String) googleRoom.get("floorName");
                String buildingGoogleId = (String) googleRoom.get("buildingId");

                String baseCode = resourceName != null
                        ? resourceName.trim().replace(" - ", "-").replace(" ", "-")
                        : "unknown";
                String code = baseCode
                        + (floor != null ? "-" + floor : "")
                        + (buildingGoogleId != null ? "-" + buildingGoogleId : "");

                room.setRoomGoogleId(googleId);
                room.setCode(code);
                room.setName((String) googleRoom.get("generatedResourceName"));
                room.setResourceEmail((String) googleRoom.get("resourceEmail"));
                room.setResourceType((String) googleRoom.get("resourceType"));
                room.setFloor(floor);

                Object capacity = googleRoom.get("capacity");
                if (capacity instanceof Number) {
                    room.setCapacity(((Number) capacity).intValue());
                }

                if (buildingGoogleId != null) {
                    buildingsRepository.findByBuildingGoogleId(buildingGoogleId)
                            .ifPresent(room::setBuilding);
                }

                roomsRepository.save(room);
            } catch (Exception e) {
                // Log l'erreur et continue la synchronisation des autres salles
                System.err.println("Erreur lors de la synchronisation d'une salle : " + e.getMessage());
            }
        }
    }

    public List<RoomDTO> getFiltered(
            String building,
            String type,
            Integer capacityMin,
            Integer capacityMax,
            String sort,
            String order) {

        try {
            List<Room> rooms = roomsRepository.findAll();

            if (building != null)
                rooms = rooms.stream()
                        .filter(r -> r.getBuilding() != null &&
                                building.equalsIgnoreCase(r.getBuilding().getBuildingGoogleId()))
                        .toList();

            if (type != null)
                rooms = rooms.stream()
                        .filter(r -> r.getResourceType() != null &&
                                type.equalsIgnoreCase(r.getResourceType()))
                        .toList();

            if (capacityMin != null)
                rooms = rooms.stream()
                        .filter(r -> r.getCapacity() != null && r.getCapacity() >= capacityMin)
                        .toList();

            if (capacityMax != null)
                rooms = rooms.stream()
                        .filter(r -> r.getCapacity() != null && r.getCapacity() <= capacityMax)
                        .toList();

            if (sort != null) {
                boolean asc = !"desc".equalsIgnoreCase(order);
                rooms = rooms.stream().sorted((a, b) -> {
                    int cmp = switch (sort.toLowerCase()) {
                        case "capacity" -> {
                            int ca = a.getCapacity() != null ? a.getCapacity() : 0;
                            int cb = b.getCapacity() != null ? b.getCapacity() : 0;
                            yield Integer.compare(ca, cb);
                        }
                        case "name" -> {
                            String na = a.getName() != null ? a.getName() : "";
                            String nb = b.getName() != null ? b.getName() : "";
                            yield na.compareToIgnoreCase(nb);
                        }
                        default -> 0;
                    };
                    return asc ? cmp : -cmp;
                }).toList();
            }

            return rooms.stream().map(roomMapper::toDTO).toList();
        } catch (Exception e) {
            throw new ApiException(ErrorCode.DATABASE_ERROR, "Erreur lors de la récupération des salles filtrées");
        }
    }

    public List<RoomDTO> getByBuilding(String buildingGoogleId) {
        if (buildingGoogleId==null || buildingGoogleId.isBlank()) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "L'ID Google du bâtiment est requis");
        }
        try {
            return roomsRepository.findByBuildingBuildingGoogleId(buildingGoogleId)
                    .stream()
                    .map(roomMapper::toDTO)
                    .toList();
        } catch (Exception e) {
            throw new ApiException(ErrorCode.DATABASE_ERROR, "Erreur lors de la récupération des salles du bâtiment");
        }
    }

      /**
     * Récupère les détails d'une salle avec son statut et ses événements
     * @param code code de la salle
     * @return Map contenant les informations détaillées
     */
    public Map<String, Object> getRoomWithStatusAndEvents(String code) {
        if (code == null || code.isBlank()) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "Le code de la salle est requis");
        }

        Room room = roomsRepository.findByCode(code)
                .orElseThrow(() -> new ApiException(ErrorCode.ROOM_NOT_FOUND, "Salle introuvable : " + code));

        try {
            Optional<EventDTO> currentEvent = eventsService.getCurrentEvent(room);
            Optional<EventDTO> nextEvent = eventsService.getNextEvent(room);

            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(now.getZone());
            ZonedDateTime endOfDay = startOfDay.plusDays(1);
            List<EventDTO> scheduleToday = eventsService.getEventsForRoom(code, startOfDay, endOfDay);

            String status = currentEvent.isPresent() ? "occupied" : "available";

            Map<String, Object> roomMap = new LinkedHashMap<>();
            roomMap.put("code", room.getCode());
            roomMap.put("name", room.getName());
            roomMap.put("capacity", room.getCapacity());
            roomMap.put("type", room.getResourceType());
            roomMap.put("status", status);
            roomMap.put("current_event", currentEvent.orElse(null));
            roomMap.put("next_event", nextEvent.orElse(null));
            roomMap.put("schedule_today", scheduleToday);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("timestamp", Instant.now().toString());
            response.put("room", roomMap);

            return response;
        } catch (Exception e) {
            throw new ApiException(ErrorCode.DATABASE_ERROR, "Erreur lors de la récupération des événements de la salle");
        }
    }
}