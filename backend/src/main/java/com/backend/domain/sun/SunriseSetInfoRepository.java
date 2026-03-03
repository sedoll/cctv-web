package com.backend.domain.sun;

import com.backend.domain.region.RegionLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SunriseSetInfoRepository extends JpaRepository<SunriseSetInfo, Long> {
    Optional<SunriseSetInfo> findByLocdateAndRegionLocation(String locdate, RegionLocation regionLocation);

    Optional<SunriseSetInfo> findTopByRegionLocationOrderByLocdateDesc(RegionLocation regionLocation);
}
