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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CctvScheduler {

    private final CctvRepository cctvRepository;
    private final PlatformTransactionManager transactionManager;

    @Value("${api-key.its}")
    private String apiKey;

    // 기본 타입 설정
    private final String TYPE_VIDEO = "5"; // MP4
    private final String TYPE_THUMB = "3"; // 정지 영상
    private final List<String> ROAD_TYPES = Arrays.asList("ex", "its");
    // 경도 (Longitude) - 동단/서단 기준
    private final float MIN_X = 124.61f; // 백령도 부근
    private final float MAX_X = 131.87f; // 독도 부근
    // 위도 (Latitude) - 남단/북단 기준
    private final float MIN_Y = 33.11f;  // 마라도 부근
    private final float MAX_Y = 38.61f;  // 강원도 고성 부근

    /**
     * 실시간 영상 URL 및 썸네일 업데이트 (통합 실행)
     */
    @Scheduled(cron = "0 0/10 * * * *")
    public void fetchAndSaveCctvDataAndThumbnail() {
        log.info("🔄 [스케줄러] 실시간 영상 데이터 및 썸네일 업데이트 시작...");
        for (String roadType : ROAD_TYPES) {
            syncCctvData(TYPE_VIDEO, roadType);
            syncCctvData(TYPE_THUMB, roadType);
        }
    }

    private void syncCctvData(String targetType, String roadType) {
        try {
            // 1. 외부 API 호출 (트랜잭션 없이 수행하여 DB 커넥션 점유 최소화)
            String url = String.format(
                    "https://openapi.its.go.kr:9443/cctvInfo?apiKey=%s&type=%s&cctvType=%s&minX=%f&maxX=%f&minY=%f&maxY=%f&getType=json",
                    apiKey, roadType, targetType, MIN_X, MAX_X, MIN_Y, MAX_Y
            );

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(new URI(url), String.class);
            List<Cctv> apiCctvList = parseJsonData(response, roadType);

            if (apiCctvList.isEmpty()) return;

            // 2. DB 저장 작업 (별도의 트랜잭션으로 실행)
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.execute(status -> {
                // DB 전체 데이터를 이름 기반으로 맵핑
                Map<String, Cctv> dbCctvMap = cctvRepository.findAll().stream()
                        .collect(Collectors.toMap(
                                Cctv::getCctvName,
                                Function.identity(),
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
                                    apiData.getCctvType(),
                                    roadType
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
                return null;
            });

            log.info("✅ [{} - {}] 동기화 완료", roadType, targetType.equals(TYPE_THUMB) ? "썸네일" : "영상데이터");

        } catch (Exception e) {
            log.error("❌ 데이터 동기화 에러", e);
        }
    }

    private List<Cctv> parseJsonData(String jsonString, String roadType) {
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
                        .roadType(roadType)
                        .build();

                list.add(cctv);
            }
        } catch (Exception e) {
            log.error("JSON 파싱 에러", e);
        }
        return list;
    }
}