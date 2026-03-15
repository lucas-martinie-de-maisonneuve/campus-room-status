package com.CampusRoomStatus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "app_metadata")
public class AppMetadata {

    @Id
    private String key;
    private String value;
    private ZonedDateTime updatedAt;
}