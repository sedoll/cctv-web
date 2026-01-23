package com.backend.domain.community;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DriverPostRepository extends JpaRepository<DriverPost, Long> {
    List<DriverPost> findAllByOrderByCreatedAtDesc();
}