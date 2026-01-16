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
@RequestMapping("/api/boards/{boardId}/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 목록
    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @PathVariable Long boardId,
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(commentService.getComments(boardId, postId));
    }

    // 댓글 작성
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CommentCreateRequestDto request
    ) {
        return ResponseEntity.ok(commentService.createComment(boardId,postId,user,request));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user
    ) {
        commentService.deleteComment(boardId, postId, commentId, user);
        return ResponseEntity.noContent().build();
    }
}
