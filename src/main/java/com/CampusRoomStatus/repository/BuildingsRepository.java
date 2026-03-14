package com.CampusRoomStatus.repository;

import com.CampusRoomStatus.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildingsRepository extends JpaRepository<Building, Integer> {

    Optional<Building> findByBuildingGoogleId(String buildingGoogleId);
}