package com.CampusRoomStatus.mapper;

import com.CampusRoomStatus.dto.RoomDTO;
import com.CampusRoomStatus.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public RoomDTO toDTO(Room room) {
        RoomDTO.BuildingRefDTO buildingRef = null;

        if (room.getBuilding() != null) {
            buildingRef = new RoomDTO.BuildingRefDTO(
                    room.getBuilding().getBuildingGoogleId(),
                    room.getBuilding().getBuildingName()
            );
        }

        return new RoomDTO(
                room.getCode(),
                room.getName(),
                buildingRef,
                room.getFloor(),
                room.getCapacity(),
                room.getResourceType()
        );
    }
}