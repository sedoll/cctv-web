package com.backend.domain.traffic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrafficEventRepository extends JpaRepository<TrafficEvent, String> {
    Page<TrafficEvent> findByEventTypeContainingIgnoreCaseOrMessageContainingIgnoreCase(String eventType, String message, Pageable pageable);

    Page<TrafficEvent> findByEventTypeContainingIgnoreCase(String eventType, Pageable pageable);

    Page<TrafficEvent> findByMessageContainingIgnoreCase(String message, Pageable pageable);
}