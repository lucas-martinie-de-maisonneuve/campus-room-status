package com.CampusRoomStatus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonPropertyOrder({"id", "name", "address", "floors"})
public class BuildingDTO {

    @JsonProperty("id")
    private String buildingGoogleId;

    @JsonProperty("name")
    private String buildingName;

    @JsonProperty("address")
    private Map<String, String> buildingAddress;

    @JsonProperty("floors")
    private List<String> buildingFloors;

}