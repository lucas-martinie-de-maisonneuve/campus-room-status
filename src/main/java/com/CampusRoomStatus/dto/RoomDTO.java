package com.CampusRoomStatus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RoomDTO {

    @JsonProperty("code")
    private final String code;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("building")
    private final BuildingRefDTO building;

    @JsonProperty("floor")
    private final String floor;

    @JsonProperty("capacity")
    private final Integer capacity;

    @JsonProperty("type")
    private final String resourceType;

    public RoomDTO(String code, String name, BuildingRefDTO building, String floor, Integer capacity, String resourceType) {
        this.code = code;
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.capacity = capacity;
        this.resourceType = resourceType;
    }

    @Getter
    public static class BuildingRefDTO {
        private final String id;
        private final String name;

        public BuildingRefDTO(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}