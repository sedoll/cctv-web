package com.backend.dto.sun;

import com.backend.domain.sun.SunriseSetInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SunriseSetTodayResponseDto {
    private String locdate;
    private String location;
    private String sunrise;
    private String suntransit;
    private String sunset;

    public static SunriseSetTodayResponseDto from(SunriseSetInfo info) {
        return SunriseSetTodayResponseDto.builder()
                .locdate(info.getLocdate())
                .location(info.getRegionLocation().getAreaName())
                .sunrise(info.getSunrise())
                .suntransit(info.getSuntransit())
                .sunset(info.getSunset())
                .build();
    }
}