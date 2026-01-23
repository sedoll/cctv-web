package com.backend.domain.community;

import com.backend.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DriverComment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private DriverPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime createdAt;
}