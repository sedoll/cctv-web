package com.backend.controller;

import com.backend.domain.cctv.Cctv;
import com.backend.domain.cctv.CctvRepository;
import com.backend.domain.traffic.TrafficEvent;
import com.backend.dto.CctvResponseDto;
import com.backend.dto.TrafficEventResponseDto;
import com.backend.dto.sun.SunriseSetTodayResponseDto;
import com.backend.service.SunriseSetService;
import com.backend.service.TrafficService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MainController {
    private final TrafficService trafficService;
    private final SunriseSetService sunriseSetService;
    private final CctvRepository cctvRepository;

    @GetMapping("/events")
    public TrafficEventResponseDto getEvents(@RequestParam(name = "pageIndex", defaultValue = "1") int page,
                                             @RequestParam(name = "pageSize", defaultValue = "10") int size,
                                             @RequestParam(name = "searchType", defaultValue = "all") String searchType,
                                             @RequestParam(name = "query", required = false) String query) {
        int pageNumber = (page > 0) ? page - 1 : 0;
        Page<TrafficEvent> eventPage = trafficService.getEventsPage(pageNumber, size, searchType, query);
        return TrafficEventResponseDto.from(eventPage);
    }

    @GetMapping("/cctvs")
    public CctvResponseDto getCctvs(@RequestParam(name = "pageIndex", defaultValue = "1") int page,
                                    @RequestParam(name = "pageSize", defaultValue = "8") int size,
                                    @RequestParam(name = "searchType", defaultValue = "all") String searchType,
                                    @RequestParam(name = "query", required = false) String query) {
        // 프론트엔드에서 1페이지부터 시작한다고 가정하면, 백엔드(Spring Data JPA)는 0페이지부터 시작하므로 -1 처리
        int pageNumber = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Cctv> cctvPage;

        String trimmedQuery = query == null ? "" : query.trim();
        String roadType = switch (searchType) {
            case "its" -> "its";
            case "ex" -> "ex";
            default -> null;
        };

        cctvPage = cctvRepository.searchByRoadTypeAndName(roadType, trimmedQuery, pageable);

        log.info(cctvPage.toString());
        return CctvResponseDto.from(cctvPage);
    }

    @GetMapping("/sun-times/today")
    public SunriseSetTodayResponseDto getTodaySunTimes(@RequestParam("lat") double lat,
                                                       @RequestParam("lng") double lng) {
        log.warn("lat, lng : " + lat + lng);
        return sunriseSetService.findTodayByNearestRegion(lat, lng)
                .map(SunriseSetTodayResponseDto::from)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "오늘 일출/일중/일몰 데이터가 없습니다."));
    }
}