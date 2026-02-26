package com.backend.domain.cctv;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface CctvRepository extends JpaRepository<Cctv, Long> {
    // cctv 검색
    Page<Cctv> findByCctvNameContainingIgnoreCase(String cctvName, Pageable pageable);
}