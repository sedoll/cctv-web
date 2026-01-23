package com.backend.service;

import com.backend.domain.community.*;
import com.backend.domain.member.*;
import com.backend.dto.community.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {
    private final DriverPostRepository postRepository;
    private final DriverCommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ContentSafetyService safetyService;

    public List<DriverPost> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public DriverPost getPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
    }

    public DriverPost createPost(String content, String username) {
        Member member = memberRepository.findByUsername(username).orElseThrow();
        String safeContent = safetyService.validateAndSanitize(content);
        return postRepository.save(DriverPost.builder()
                .member(member)
                .content(safeContent)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public List<CommentResponseDto> getComments(Long postId, String currentUsername) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(c -> CommentResponseDto.builder()
                        .id(c.getId())
                        .author(c.getMember().getNickname())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .isMyComment(c.getMember().getUsername().equals(currentUsername))
                        .build())
                .collect(Collectors.toList());
    }

    public CommentResponseDto createComment(Long postId, CommentRequestDto dto, String username) {
        DriverPost post = getPost(postId);
        Member member = memberRepository.findByUsername(username).orElseThrow();
        String safeContent = safetyService.validateAndSanitize(dto.getContent());

        DriverComment saved = commentRepository.save(DriverComment.builder()
                .post(post)
                .member(member)
                .content(safeContent)
                .createdAt(LocalDateTime.now())
                .build());

        return CommentResponseDto.builder()
                .id(saved.getId())
                .author(saved.getMember().getNickname())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .isMyComment(true)
                .build();
    }

    public void deleteComment(Long commentId, String username) {
        DriverComment comment = commentRepository.findById(commentId).orElseThrow();
        if (!comment.getMember().getUsername().equals(username)) {
            throw new IllegalArgumentException("권한 없음");
        }
        commentRepository.delete(comment);
    }

    // updateComment는 생략 (createComment와 유사)
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto dto, String username) {
        DriverComment comment = commentRepository.findById(commentId).orElseThrow();
        if (!comment.getMember().getUsername().equals(username)) throw new IllegalArgumentException("권한 없음");

        String safeContent = safetyService.validateAndSanitize(dto.getContent());
        comment.setContent(safeContent);

        return CommentResponseDto.builder()
                .id(comment.getId())
                .author(comment.getMember().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .isMyComment(true)
                .build();
    }
}