package com.CampusRoomStatus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String eventGoogleId;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private String title;

    private String organizerEmail;

    @Column(nullable = false)
    private ZonedDateTime startTime;

    @Column(nullable = false)
    private ZonedDateTime endTime;

    @Column(nullable = false)
    private String status;

    private ZonedDateTime lastSyncedAt;
}