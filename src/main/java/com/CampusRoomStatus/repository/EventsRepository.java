package com.CampusRoomStatus.repository;

import com.CampusRoomStatus.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventsRepository extends JpaRepository<Event, Integer> {

    Optional<Event> findByEventGoogleId(String eventGoogleId);

    List<Event> findByRoomIdAndStartTimeBetween(Integer roomId, ZonedDateTime start, ZonedDateTime end);

    List<Event> findByRoomIdAndStartTimeBetweenOrderByStartTimeAsc(Integer roomId, ZonedDateTime start, ZonedDateTime end);
}