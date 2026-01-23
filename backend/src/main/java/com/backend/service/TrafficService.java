package com.backend.service;

import com.backend.domain.traffic.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrafficService {
    private final TrafficEventRepository repository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void syncEvents() {
        // 실제 API 호출 로직 (WebClient) 구현 필요
        // TrafficEventDto -> TrafficEvent 변환 후 repository.save()
    }

    public List<TrafficEvent> getAllEvents() {
        return repository.findAll();
    }
}