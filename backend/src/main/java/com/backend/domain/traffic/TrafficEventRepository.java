package com.backend.domain.traffic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrafficEventRepository extends JpaRepository<TrafficEvent, String> {}