package com.backend.domain.community;

import com.backend.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DriverPost {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime createdAt;
}