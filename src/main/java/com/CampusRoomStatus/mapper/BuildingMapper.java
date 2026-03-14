package com.CampusRoomStatus.mapper;

import com.CampusRoomStatus.dto.BuildingDTO;
import com.CampusRoomStatus.entity.Building;
import org.springframework.stereotype.Component;

@Component
public class BuildingMapper {

    public BuildingDTO toDTO(Building building) {
        BuildingDTO dto = new BuildingDTO();
        dto.setBuildingGoogleId(building.getBuildingGoogleId());
        dto.setBuildingName(building.getBuildingName());
        dto.setBuildingFloors(building.getBuildingFloors());
        dto.setBuildingAddress(building.getBuildingAddress());
        return dto;
    }
}