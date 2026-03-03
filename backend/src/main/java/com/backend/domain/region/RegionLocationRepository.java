package com.backend.domain.region;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RegionLocationRepository extends JpaRepository<RegionLocation, Integer> {

    // 지역 이름으로 위경도 좌표를 찾고 싶을 때 사용하는 메서드
    Optional<RegionLocation> findByAreaName(String areaName);

}