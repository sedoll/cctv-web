package com.backend.domain.community;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DriverCommentRepository extends JpaRepository<DriverComment, Long> {
    List<DriverComment> findByPostIdOrderByCreatedAtAsc(Long postId);
}