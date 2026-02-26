package com.backend.service;

import com.backend.domain.traffic.TrafficEvent;
import com.backend.domain.traffic.TrafficEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrafficService {
    private final TrafficEventRepository repository;

    @Value("${api-key.its}")
    private String apiKey;

    /**
     * 매 시간 정각(00분 00초)에 돌발상황 정보를 동기화한다.
     */
    @Scheduled(cron = "0 0,30 * * * *")
    @Transactional
    public void syncEvents() {
        try {
            String url = String.format(
                    "https://openapi.its.go.kr:9443/eventInfo?apiKey=%s&type=all&eventType=all&getType=json",
                    apiKey
            );

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(new URI(url), String.class);

            List<TrafficEvent> apiEvents = parseEvents(response);
            if (apiEvents.isEmpty()) {
                log.info("[Traffic] 동기화할 돌발상황 데이터가 없습니다.");
                return;
            }

            Map<String, TrafficEvent> dbEventsMap = repository.findAll().stream()
                    .collect(Collectors.toMap(this::makeCompareKey, Function.identity(), (a, b) -> a));

            List<TrafficEvent> insertTargets = new ArrayList<>();
            List<TrafficEvent> updateTargets = new ArrayList<>();

            for (TrafficEvent apiEvent : apiEvents) {
                String compareKey = makeCompareKey(apiEvent);
                TrafficEvent existing = dbEventsMap.get(compareKey);

                // startDate + roadNo + message 기준 데이터가 없으면 신규 저장
                if (existing == null) {
                    apiEvent.setUpdateTime(LocalDateTime.now());
                    insertTargets.add(apiEvent);
                    continue;
                }

                // startDate + roadNo + message 동일 + endDate 변경 시 endDate만 업데이트
                if (!safe(existing.getEndDate()).equals(safe(apiEvent.getEndDate()))) {
                    existing.setEndDate(apiEvent.getEndDate());
                    existing.setUpdateTime(LocalDateTime.now());
                    updateTargets.add(existing);
                }
            }

            if (!insertTargets.isEmpty()) {
                repository.saveAll(insertTargets);
            }
            if (!updateTargets.isEmpty()) {
                repository.saveAll(updateTargets);
            }

            log.info("[Traffic] 돌발상황 동기화 완료: 수집 {}건, 신규 {}건, endDate 갱신 {}건",
                    apiEvents.size(), insertTargets.size(), updateTargets.size());
        } catch (Exception e) {
            log.error("[Traffic] 돌발상황 동기화 실패", e);
        }
    }

    public Page<TrafficEvent> getEventsPage(int page, int size) {
        return getEventsPage(page, size, "all", "");
    }

    public Page<TrafficEvent> getEventsPage(int page, int size, String searchType, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startDate"));
        String trimmedKeyword = keyword == null ? "" : keyword.trim();

        if (trimmedKeyword.isEmpty()) {
            return repository.findAll(pageable);
        }

        String normalizedType = searchType == null ? "all" : searchType.trim().toLowerCase();

        return switch (normalizedType) {
            case "event_type" -> repository.findByEventTypeContainingIgnoreCase(trimmedKeyword, pageable);
            case "message" -> repository.findByMessageContainingIgnoreCase(trimmedKeyword, pageable);
            case "all" -> repository.findByEventTypeContainingIgnoreCaseOrMessageContainingIgnoreCase(trimmedKeyword, trimmedKeyword, pageable);
            default -> repository.findByEventTypeContainingIgnoreCaseOrMessageContainingIgnoreCase(trimmedKeyword, trimmedKeyword, pageable);
        };
    }


    private List<TrafficEvent> parseEvents(String jsonString) {
        List<TrafficEvent> results = new ArrayList<>();

        if (jsonString == null || jsonString.isBlank()) {
            return results;
        }

        JSONObject root = new JSONObject(jsonString);
        JSONObject header = root.optJSONObject("header");
        if (header == null || header.optInt("resultCode", -1) != 0) {
            log.warn("[Traffic] API 응답 실패: {}", header != null ? header.optString("resultMsg") : "header 없음");
            return results;
        }

        JSONObject body = root.optJSONObject("body");
        if (body == null) {
            return results;
        }

        JSONArray items = body.optJSONArray("items");
        if (items == null) {
            return results;
        }

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String startDate = item.optString("startDate", "");
            String roadNo = item.optString("roadNo", "");
            String message = item.optString("message", "");
            String endDate = item.optString("endDate", "");

            TrafficEvent event = TrafficEvent.builder()
                    .eventId(makeEventId(startDate, roadNo, message, endDate))
                    .type(item.optString("type", ""))
                    .eventType(item.optString("eventType", ""))
                    .eventDetailType(item.optString("eventDetailType", ""))
                    .startDate(startDate)
                    .endDate(endDate)
                    .coordX(parseDouble(item.optString("coordX", "0")))
                    .coordY(parseDouble(item.optString("coordY", "0")))
                    .linkId(item.optString("linkId", ""))
                    .roadName(item.optString("roadName", ""))
                    .roadNo(roadNo)
                    .roadDrcType(item.optString("roadDrcType", ""))
                    .lanesBlockType(item.optString("lanesBlockType", ""))
                    .lanesBlocked(item.optString("lanesBlocked", ""))
                    .message(message)
                    .updateTime(LocalDateTime.now())
                    .build();

            results.add(event);
        }

        return results;
    }

    private String makeCompareKey(TrafficEvent event) {
        return String.join("|", safe(event.getStartDate()), safe(event.getRoadNo()), safe(event.getMessage()));
    }

    private String makeEventId(String startDate, String roadNo, String message, String endDate) {
        return String.join("|", safe(startDate), safe(roadNo), safe(message), safe(endDate));
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}