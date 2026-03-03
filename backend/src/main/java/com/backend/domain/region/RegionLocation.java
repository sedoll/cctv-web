package com.backend.domain.region;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "region_location")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제어 (JPA 스펙)
public class RegionLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "area_name", nullable = false, length = 50)
    private String areaName;

    @Column(nullable = false)
    private Float lat;

    @Column(nullable = false)
    private Float lng;

    @Builder
    public RegionLocation(String areaName, Float lat, Float lng) {
        this.areaName = areaName;
        this.lat = lat;
        this.lng = lng;
    }
}
