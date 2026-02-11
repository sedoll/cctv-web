package com.backend.scheduler;

import com.backend.domain.cctv.Cctv;
import com.backend.domain.cctv.CctvRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CctvScheduler {

    private final CctvRepository cctvRepository;

    @Value("${api-key.its}")
    private String apiKey;

    // 기본 타입 설정
    private final String TYPE_VIDEO = "5"; // MP4
    private final String TYPE_THUMB = "3"; // 정지 영상
    private final String ROAD_TYPE = "ex";

    /**
     * 실시간 영상 URL 업데이트 (매일 밤 10시 실행 - 기존 유지)
     */
    @Scheduled(cron = "0 13 22 * * *")
    @Transactional
    public void fetchAndSaveCctvData() {
        log.info("🔄 [스케줄러] 실시간 영상 데이터 업데이트 시작...");
        syncCctvData(TYPE_VIDEO);
    }

    /**
     * 썸네일 추출 (매일 오전 9시, 오후 6시 실행)
     */
    @Scheduled(cron = "0 13 9,22 * * *")
    @Transactional
    public void fetchAndSaveThumbnail() {
        log.info("📸 [스케줄러] CCTV 썸네일 업데이트 시작...");
        syncCctvData(TYPE_THUMB);
    }

    private void syncCctvData(String targetType) {
        try {
            String url = String.format(
                    "https://openapi.its.go.kr:9443/cctvInfo?apiKey=%s&type=%s&cctvType=%s&minX=126.7&maxX=127.2&minY=37.4&maxY=37.7&getType=json",
                    apiKey, ROAD_TYPE, targetType
            );

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(new URI(url), String.class);
            List<Cctv> apiCctvList = parseJsonData(response);

            if (apiCctvList.isEmpty()) return;

            // DB 전체 데이터를 이름 기반으로 맵핑
            java.util.Map<String, Cctv> dbCctvMap = cctvRepository.findAll().stream()
                    .collect(java.util.stream.Collectors.toMap(
                            Cctv::getCctvName,
                            java.util.function.Function.identity(),
                            (existing, replacement) -> existing
                    ));

            List<Cctv> newCctvs = new ArrayList<>();

            for (Cctv apiData : apiCctvList) {
                String name = apiData.getCctvName();

                if (dbCctvMap.containsKey(name)) {
                    Cctv existing = dbCctvMap.get(name);

                    if (TYPE_THUMB.equals(targetType)) {
                        // ✅ 썸네일 업데이트 로직
                        existing.updateThumbnail(apiData.getCctvUrl());
                    } else {
                        // ✅ 영상 URL 및 기본 정보 업데이트 로직
                        existing.updateData(
                                apiData.getCctvUrl(),
                                apiData.getCoordX(),
                                apiData.getCoordY(),
                                apiData.getCctvType()
                        );
                    }
                } else if (TYPE_VIDEO.equals(targetType)) {
                    // 영상 URL 동기화 때만 신규 추가 (썸네일 때는 이름이 없으면 무시)
                    newCctvs.add(apiData);
                }
            }

            if (!newCctvs.isEmpty()) {
                cctvRepository.saveAll(newCctvs);
            }

            log.info("✅ [{}] 동기화 완료", targetType.equals(TYPE_THUMB) ? "썸네일" : "영상데이터");

        } catch (Exception e) {
            log.error("❌ 데이터 동기화 에러", e);
        }
    }

    private List<Cctv> parseJsonData(String jsonString) {
        List<Cctv> list = new ArrayList<>();
        try {
            // String -> JSON 변환
            JSONObject root = new JSONObject(jsonString);

            // response 객체 꺼내기
            if (!root.has("response")) return list;
            JSONObject response = root.getJSONObject("response");

            // data 배열 꺼내기
            if (!response.has("data")) return list;
            JSONArray dataArray = response.getJSONArray("data");

            // 배열 반복문 (여기가 1개만 저장되던 문제 해결의 핵심)
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.getJSONObject(i);

                Cctv cctv = Cctv.builder()
                        .cctvName(item.optString("cctvname", "Unknown"))
                        .cctvUrl(item.optString("cctvurl", ""))
                        .coordX(item.optDouble("coordx", 0.0))
                        .coordY(item.optDouble("coordy", 0.0))
                        .cctvType(item.optInt("cctvtype", 1))
                        .build();

                list.add(cctv);
            }
        } catch (Exception e) {
            log.error("JSON 파싱 에러", e);
        }
        return list;
    }
}