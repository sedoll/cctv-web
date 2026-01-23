package com.backend.controller;

import com.backend.domain.community.DriverPost;
import com.backend.dto.community.*;
import com.backend.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;

    @GetMapping("/posts")
    public List<DriverPost> getPosts() {
        return communityService.getAllPosts();
    }

    @GetMapping("/posts/{postId}")
    public DriverPost getPost(@PathVariable Long postId) {
        return communityService.getPost(postId);
    }

    @PostMapping("/posts")
    public DriverPost createPost(@RequestBody CommentRequestDto dto, Authentication auth) {
        // 본문만 담긴 DTO 재활용
        return communityService.createPost(dto.getContent(), auth.getName());
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentResponseDto> getComments(@PathVariable Long postId, Authentication auth) {
        String username = (auth != null) ? auth.getName() : null;
        return communityService.getComments(postId, username);
    }

    @PostMapping("/posts/{postId}/comments")
    public CommentResponseDto addComment(@PathVariable Long postId, @RequestBody CommentRequestDto dto, Authentication auth) {
        return communityService.createComment(postId, dto, auth.getName());
    }

    @PutMapping("/comments/{commentId}")
    public CommentResponseDto updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDto dto, Authentication auth) {
        return communityService.updateComment(commentId, dto, auth.getName());
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId, Authentication auth) {
        communityService.deleteComment(commentId, auth.getName());
        return ResponseEntity.ok("삭제됨");
    }
}