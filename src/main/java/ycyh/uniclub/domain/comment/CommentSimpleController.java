package ycyh.uniclub.domain.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.user.User;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentSimpleController {

    private final CommentService commentService;

    // /api/posts/{postId}/comments 경로 지원

    // 댓글 목록 조회 (postId만으로)
    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    // 댓글 작성 (postId만으로)
    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CommentCreateRequestDto request
    ) {
        return ResponseEntity.ok(commentService.createCommentByPostId(postId, user, request));
    }

    // /api/comments/{commentId} 경로 지원

    // 댓글 수정 (commentId만으로)
    @PutMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CommentUpdateRequestDto request
    ) {
        return ResponseEntity.ok(commentService.updateCommentById(commentId, user, request));
    }

    // 댓글 삭제 (commentId만으로)
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user
    ) {
        commentService.deleteCommentById(commentId, user);
        return ResponseEntity.noContent().build();
    }
}
