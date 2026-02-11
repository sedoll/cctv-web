package com.backend.domain.cctv; // 👈 패키지 경로 확인!

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CctvRepository extends JpaRepository<Cctv, Long> {
    // 필요한 쿼리 메서드가 있다면 여기에 추가
    // 예: 특정 이름이 포함된 CCTV 찾기
    // List<Cctv> findByCctvNameContaining(String name);
}