package com.CampusRoomStatus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String roomGoogleId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String resourceEmail;
    private String resourceType;
    private String floor;
    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;
}