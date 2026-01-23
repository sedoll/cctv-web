package com.backend.dto.community;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter @Builder
public class CommentResponseDto {
    private Long id;
    private String author;
    private String content;
    private boolean isMyComment;
    private LocalDateTime createdAt;
}