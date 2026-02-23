package com.backend.domain.cctv; // 👈 패키지 경로 확인!

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "cctv")
@EntityListeners(AuditingEntityListener.class)
public class Cctv {

    @Id // PK 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 1부터 자동 증가 (Auto Increment)
    @Column(name = "id")
    private Long id;

    @Column(name = "cctv_name")
    private String cctvName; // CCTV 이름 (예: 강남대로)

    @Column(name = "cctv_url", length = 1000)
    private String cctvUrl; // 스트리밍 URL

    @Column(name = "coord_x")
    private Double coordX; // 경도 (X좌표)

    @Column(name = "coord_y")
    private Double coordY; // 위도 (Y좌표)

    @Column(name = "cctv_type")
    private Integer cctvType; // JSON에 있는 cctvtype (1, 2, 3...)

    @Column(name="cctv_thumbnail")
    private String thumbnailUrl; // 썸네일 URL 필드 추가

    @Column(name = "road_type")
    private String roadType; // 도로 타입 (ex: 고속도로, its: 국도)

    @CreatedDate
    @Column(name = "create_date", updatable = false)
    private LocalDateTime createDate; // 최초 생성일

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate; // 최근 수정일

    public void updateThumbnail(String url) {
        this.thumbnailUrl = url;
    }

    public void updateData(String url, Double x, Double y, Integer type, String roadType) {
        this.cctvUrl = url;
        this.coordX = x;
        this.coordY = y;
        this.cctvType = type; // cctvType 필드 업데이트 추가
        this.roadType = roadType;
    }
}