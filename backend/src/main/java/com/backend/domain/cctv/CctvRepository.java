package com.backend.domain.cctv;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CctvRepository extends JpaRepository<Cctv, Long> {
    @Query("""
            SELECT c
            FROM Cctv c
            WHERE (:roadType IS NULL OR c.roadType = :roadType OR (:roadType = 'ex' AND c.roadType IS NULL))
              AND (:query = '' OR LOWER(c.cctvName) LIKE LOWER(CONCAT('%', :query, '%')))
            """)
    Page<Cctv> searchByRoadTypeAndName(@Param("roadType") String roadType,
                                       @Param("query") String query,
                                       Pageable pageable);
}