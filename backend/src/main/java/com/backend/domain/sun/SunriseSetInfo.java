package com.backend.domain.sun;

import com.backend.domain.region.RegionLocation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "sunrise_set_info",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_sunrise_set_locdate_region", columnNames = {"locdate", "region_location_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SunriseSetInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 8)
    private String locdate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_location_id", nullable = false)
    private RegionLocation regionLocation;

    @Column(nullable = false, length = 6)
    private String sunrise;

    @Column(nullable = false, length = 6)
    private String suntransit;

    @Column(nullable = false, length = 6)
    private String sunset;

    @CreatedDate
    @Column(name = "create_date", updatable = false)
    private LocalDateTime createDate; // 최초 생성일

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate; // 최근 수정일

    @Builder
    public SunriseSetInfo(String locdate, RegionLocation regionLocation, String sunrise, String suntransit, String sunset) {
        this.locdate = locdate;
        this.regionLocation = regionLocation;
        this.sunrise = sunrise;
        this.suntransit = suntransit;
        this.sunset = sunset;
    }

    public void updateTimes(String sunrise, String suntransit, String sunset) {
        this.sunrise = sunrise;
        this.suntransit = suntransit;
        this.sunset = sunset;
    }
}