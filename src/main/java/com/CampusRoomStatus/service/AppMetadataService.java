package com.CampusRoomStatus.service;

import com.CampusRoomStatus.entity.AppMetadata;
import com.CampusRoomStatus.repository.AppMetadataRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class AppMetadataService {

    private final AppMetadataRepository repository;

    public AppMetadataService(AppMetadataRepository repository) {
        this.repository = repository;
    }

    public boolean isSynced(String key) {
        return repository.findById(key).isPresent();
    }

    public void markSynced(String key) {
        AppMetadata meta = repository.findById(key).orElse(new AppMetadata());
        meta.setKey(key);
        meta.setValue("synced");
        meta.setUpdatedAt(ZonedDateTime.now());
        repository.save(meta);
    }

    public ZonedDateTime getLastSync(String key) {
        return repository.findById(key)
                .map(AppMetadata::getUpdatedAt)
                .orElse(null);
    }
}