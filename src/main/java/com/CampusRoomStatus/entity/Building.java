package com.CampusRoomStatus.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "buildings")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String buildingGoogleId;

    @Column(nullable = false)
    private String buildingName;

    @Column(columnDefinition = "TEXT[]")
    private List<String> buildingFloors;

    @Type(JsonType.class)
    @Column(columnDefinition = "JSONB")
    private Map<String, String> buildingAddress;
}