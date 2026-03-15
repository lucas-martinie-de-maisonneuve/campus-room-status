package com.CampusRoomStatus.repository;

import com.CampusRoomStatus.entity.AppMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppMetadataRepository extends JpaRepository<AppMetadata, String> {}