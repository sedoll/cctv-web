package com.backend.controller;

import com.backend.domain.traffic.TrafficEvent;
import com.backend.service.TrafficService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MainController {
    private final TrafficService trafficService;

    @GetMapping("/events")
    public List<TrafficEvent> getEvents() {
        return trafficService.getAllEvents();
    }

    // CCTV 등 추가 API...
}