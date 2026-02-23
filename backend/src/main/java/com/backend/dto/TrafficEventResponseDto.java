package com.backend.dto;

import com.backend.domain.traffic.TrafficEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficEventResponseDto {
    private List<TrafficEvent> events;
    private long totalElements;
    private int totalPages;
    private int currentPage;

    public static TrafficEventResponseDto from(Page<TrafficEvent> page) {
        return TrafficEventResponseDto.builder()
                .events(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber() + 1)
                .build();
    }
}