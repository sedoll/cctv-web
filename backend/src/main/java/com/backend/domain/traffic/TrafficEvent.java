package com.backend.domain.traffic;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TrafficEvent {
    @Id
    private String eventId;

    // 도로 정보
    private String type;
    private String eventType;
    private String eventDetailType;

    // 위치/링크 정보
    private String linkId;
    private String roadName;
    private String roadNo;
    private String roadDrcType;
    private String message;
    private String lanesBlockType;
    private String lanesBlocked;

    // 시간 정보 (원문 + 파싱 결과)
    private String startDate;
    private String endDate;

    private Double coordX;
    private Double coordY;

    // 마지막 동기화 시각
    private LocalDateTime updateTime;

    public boolean needsUpdate(TrafficEvent newData) {
        return !Objects.equals(this.message, newData.getMessage())
                || !Objects.equals(this.type, newData.getType())
                || !Objects.equals(this.eventType, newData.getEventType())
                || !Objects.equals(this.eventDetailType, newData.getEventDetailType())
                || !Objects.equals(this.startDate, newData.getStartDate())
                || !Objects.equals(this.endDate, newData.getEndDate())
                || !Objects.equals(this.coordX, newData.getCoordX())
                || !Objects.equals(this.coordY, newData.getCoordY())
                || !Objects.equals(this.linkId, newData.getLinkId())
                || !Objects.equals(this.roadName, newData.getRoadName())
                || !Objects.equals(this.roadNo, newData.getRoadNo())
                || !Objects.equals(this.roadDrcType, newData.getRoadDrcType())
                || !Objects.equals(this.lanesBlockType, newData.getLanesBlockType())
                || !Objects.equals(this.lanesBlocked, newData.getLanesBlocked());
    }
}