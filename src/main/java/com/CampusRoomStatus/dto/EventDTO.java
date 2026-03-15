package com.CampusRoomStatus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class EventDTO {

    @JsonProperty("title")
    private final String title;

    @JsonProperty("start")
    private final ZonedDateTime start;

    @JsonProperty("end")
    private final ZonedDateTime end;

    @JsonProperty("organizer")
    private final String organizerEmail;

    @JsonProperty("status")
    private final String status;

    public EventDTO(String title, ZonedDateTime start, ZonedDateTime end, String organizerEmail, String status) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.organizerEmail = organizerEmail;
        this.status = status;
    }
}