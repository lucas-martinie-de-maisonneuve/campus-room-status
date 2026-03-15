package com.CampusRoomStatus.mapper;

import com.CampusRoomStatus.dto.EventDTO;
import com.CampusRoomStatus.entity.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventDTO toDTO(Event event) {
        return new EventDTO(
                event.getTitle(),
                event.getStartTime(),
                event.getEndTime(),
                event.getOrganizerEmail(),
                event.getStatus()
        );
    }
}