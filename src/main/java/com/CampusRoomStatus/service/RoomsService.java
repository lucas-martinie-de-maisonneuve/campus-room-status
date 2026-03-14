package com.CampusRoomStatus.service;

import com.CampusRoomStatus.dto.RoomDTO;
import com.CampusRoomStatus.entity.Room;
import com.CampusRoomStatus.integration.google.GoogleDirectoryOAuthClient;
import com.CampusRoomStatus.mapper.RoomMapper;
import com.CampusRoomStatus.repository.BuildingsRepository;
import com.CampusRoomStatus.repository.RoomsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RoomsService {

    private final RoomsRepository roomsRepository;
    private final BuildingsRepository buildingsRepository;
    private final GoogleDirectoryOAuthClient googleDirectoryOAuthClient;
    private final RoomMapper roomMapper;

    public RoomsService(
            RoomsRepository roomsRepository,
            BuildingsRepository buildingsRepository,
            GoogleDirectoryOAuthClient googleDirectoryOAuthClient,
            RoomMapper roomMapper) {
        this.roomsRepository = roomsRepository;
        this.buildingsRepository = buildingsRepository;
        this.googleDirectoryOAuthClient = googleDirectoryOAuthClient;
        this.roomMapper = roomMapper;
    }

    public List<RoomDTO> getAll() {
        return roomsRepository.findAll()
                .stream()
                .map(roomMapper::toDTO)
                .toList();
    }

    public Optional<RoomDTO> getByCode(String code) {
        return roomsRepository.findByCode(code)
                .map(roomMapper::toDTO);
    }

    @SuppressWarnings("unchecked")
    public void syncFromGoogle() {
        Map<String, Object> response = googleDirectoryOAuthClient.listRooms();

        if (response == null || !response.containsKey("items")) {
            return;
        }

        List<Map<String, Object>> googleRooms = (List<Map<String, Object>>) response.get("items");

        for (Map<String, Object> googleRoom : googleRooms) {
            String googleId = String.valueOf(googleRoom.get("resourceId"));

            Room room = roomsRepository.findByRoomGoogleId(googleId)
                    .orElse(new Room());

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
            if (capacity != null) {
                room.setCapacity(((Number) capacity).intValue());
            }

            if (buildingGoogleId != null) {
                buildingsRepository.findByBuildingGoogleId(buildingGoogleId)
                        .ifPresent(room::setBuilding);
            }

            roomsRepository.save(room);
        }
    }

    public List<RoomDTO> getByBuilding(String buildingGoogleId) {
        return roomsRepository.findByBuildingBuildingGoogleId(buildingGoogleId)
                .stream()
                .map(roomMapper::toDTO)
                .toList();
    }
}