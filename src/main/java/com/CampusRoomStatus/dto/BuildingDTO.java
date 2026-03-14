package com.CampusRoomStatus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BuildingDTO {

    @JsonProperty("id")
    private String buildingGoogleId;

    @JsonProperty("name")
    private String buildingName;

    @JsonProperty("floors")
    private List<String> buildingFloors;

    @JsonProperty("address")
    private Map<String, String> buildingAddress;
}