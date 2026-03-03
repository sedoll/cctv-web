package com.backend.service;

import com.backend.domain.region.RegionLocation;
import com.backend.domain.region.RegionLocationRepository;
import com.backend.domain.sun.SunriseSetInfo;
import com.backend.domain.sun.SunriseSetInfoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SunriseSetService {

    private final RegionLocationRepository regionLocationRepository;
    private final SunriseSetInfoRepository sunriseSetInfoRepository;


    // JSON 파싱을 위한 ObjectMapper
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 공공데이터포털 API KEY
    @Value("api-key.sun")
    private String ASTRONOMY_API_KEY;
    private static final DateTimeFormatter LOC_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Scheduled(cron = "0 10 0 * * *")
    @Transactional
    public void syncDailySunriseSetInfo() {
        String locdate = LocalDate.now(ZoneId.of("Asia/Seoul")).format(LOC_DATE_FORMAT);
        List<RegionLocation> regions = regionLocationRepository.findAll();

        if (regions.isEmpty()) {
            log.warn("[SunriseSet] region_location 데이터가 없어 동기화를 건너뜁니다.");
            return;
        }

        RestTemplate restTemplate = new RestTemplate();
        int successCount = 0;

        for (RegionLocation region : regions) {
            try {
                // 한글 지역명 안전하게 인코딩 (예: "강원고성" -> "%EA%B0%95%EC%9B%90%EA%B3%A0%EC%84%B1")
                String encodedLocation = URLEncoder.encode(region.getAreaName(), StandardCharsets.UTF_8);

                // URI 생성 (build(true)를 통해 파라미터 중복 인코딩 방지)
                URI uri = UriComponentsBuilder.fromHttpUrl("https://apis.data.go.kr/B090041/openapi/service/RiseSetInfoService/getAreaRiseSetInfo")
                        .queryParam("serviceKey", ASTRONOMY_API_KEY)
                        .queryParam("locdate", locdate)
                        .queryParam("location", encodedLocation)
                        .queryParam("_type", "json") // 명시적으로 JSON 응답 요청
                        .build(true)
                        .toUri();

                String jsonResponse = restTemplate.getForObject(uri, String.class);

                // API 정상 JSON 응답 여부 확인
                if (jsonResponse == null || !jsonResponse.trim().startsWith("{")) {
                    log.error("[SunriseSet] API 응답이 JSON 형식이 아닙니다. location={}, 응답: {}", region.getAreaName(), jsonResponse);
                    continue;
                }

                // JSON 파싱
                RiseSetApiRow row = parseRiseSetJson(jsonResponse);
                if (row == null) {
                    log.warn("[SunriseSet] 데이터 파싱 실패 또는 누락 - location={}, locdate={}", region.getAreaName(), locdate);
                    continue;
                }

                // DB 저장 또는 업데이트
                Optional<SunriseSetInfo> existingOpt = sunriseSetInfoRepository.findByLocdateAndRegionLocation(locdate, region);
                if (existingOpt.isPresent()) {
                    existingOpt.get().updateTimes(row.sunrise(), row.suntransit(), row.sunset());
                } else {
                    SunriseSetInfo info = SunriseSetInfo.builder()
                            .locdate(locdate)
                            .regionLocation(region)
                            .sunrise(row.sunrise())
                            .suntransit(row.suntransit())
                            .sunset(row.sunset())
                            .build();
                    sunriseSetInfoRepository.save(info);
                }
                successCount++;
            } catch (Exception e) {
                log.error("[SunriseSet] 동기화 실패 - location={}, locdate={}", region.getAreaName(), locdate, e);
            }
        }

        log.info("[SunriseSet] 일출/일중/일몰 동기화 완료 - 대상 {}건, 성공 {}건", regions.size(), successCount);
    }

    @Transactional(readOnly = true)
    public Optional<SunriseSetInfo> findTodayByNearestRegion(double lat, double lng) {
        String today = LocalDate.now(ZoneId.of("Asia/Seoul")).format(LOC_DATE_FORMAT);
        return regionLocationRepository.findAll().stream()
                .min((r1, r2) -> Double.compare(distanceSquared(lat, lng, r1), distanceSquared(lat, lng, r2)))
                .flatMap(region -> sunriseSetInfoRepository.findByLocdateAndRegionLocation(today, region)
                        .or(() -> sunriseSetInfoRepository.findTopByRegionLocationOrderByLocdateDesc(region)));
    }

    private double distanceSquared(double lat, double lng, RegionLocation region) {
        double dLat = lat - region.getLat();
        double dLng = lng - region.getLng();
        return (dLat * dLat) + (dLng * dLng);
    }

    // JSON 전용 파싱 메서드
    private RiseSetApiRow parseRiseSetJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }

        try {
            JsonNode root = objectMapper.readTree(json);
            // JSON 계층 탐색: response -> body -> items -> item
            JsonNode itemNode = root.path("response").path("body").path("items").path("item");

            // 해당 노드가 없거나 비어있는 경우
            if (itemNode.isMissingNode() || itemNode.isNull()) {
                return null;
            }

            // 공백이 포함되어 넘어올 수 있으므로 trim() 필수
            String sunrise = itemNode.path("sunrise").asText("").trim();
            String suntransit = itemNode.path("suntransit").asText("").trim();
            String sunset = itemNode.path("sunset").asText("").trim();

            if (sunrise.isEmpty() || suntransit.isEmpty() || sunset.isEmpty()) {
                return null;
            }
            return new RiseSetApiRow(sunrise, suntransit, sunset);
        } catch (Exception e) {
            log.error("[SunriseSet] JSON 파싱 중 오류", e);
            return null;
        }
    }

    // DTO 레코드 (자바 14+)
    private record RiseSetApiRow(String sunrise, String suntransit, String sunset) {
    }
}