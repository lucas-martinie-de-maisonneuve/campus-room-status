package com.CampusRoomStatus.repository;

import com.CampusRoomStatus.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface RoomsRepository extends JpaRepository<Room, Integer> {

    /**
     * Find a room by its Google Calendar resource ID.
     * This is used to sync rooms from Google Calendar and ensure we don't create duplicates.
     * @param roomGoogleId
     * @return
     */
    Optional<Room> findByRoomGoogleId(String roomGoogleId);

    /**
     * Find a room by its unique code (e.g., "A101"). This is used for API lookups and to ensure uniqueness of room codes.
     * @param code
     * @return
     */
    Optional<Room> findByCode(String code);

    /**
     * Find all rooms that belong to a specific building, identified by the building's Google Calendar resource ID.
     * @param buildingGoogleId
     * @return
     */
    List<Room> findByBuildingBuildingGoogleId(String buildingGoogleId);

}