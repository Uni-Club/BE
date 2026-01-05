package ycyh.uniclub.domain.board;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.user.User;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 특정 게시판의 게시글 목록 조회
    @GetMapping("/{boardId}/posts")
    public ResponseEntity<List<PostResponseDto>> getPostsByBoard(
            @PathVariable Long boardId
    ) {
        List<PostResponseDto> posts = postService.getPostsByBoard(boardId);
        return ResponseEntity.ok(posts);
    }

    // 게시글 작성
    @PostMapping("/{boardId}/posts")
    public ResponseEntity<PostResponseDto> createPost(
            @PathVariable("boardId") Long boardId,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid PostCreateRequestDto request
    ) {
        PostResponseDto response = postService.createPost(boardId, request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 게시글 상세조회
    @GetMapping("/{boardId}/posts/{postId}")
    public ResponseEntity<PostResponseDto> getPost(
            @PathVariable("boardId") Long boardId,
            @PathVariable("postId") Long postId
    ) {
        PostResponseDto response = postService.getPost(boardId, postId);
        return ResponseEntity.ok(response);
    }

    // 게시글 수정
    @PatchMapping("/{boardId}/posts/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable("boardId") Long boardId,
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal User user,
            @RequestBody PostUpdateRequestDto request
    ) {
        Post post = postService.getPostEntity(boardId, postId);

        // 작성자 검증
        if (!post.getAuthor().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("게시글 작성자만 수정할 수 있습니다.");
        }

        PostResponseDto response = postService.updatePost(boardId, postId, request);
        return ResponseEntity.ok(response);
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable("boardId") Long boardId,
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal User user
    ) {
        Post post = postService.getPostEntity(boardId, postId);

        //작성자 검증
        if (!post.getAuthor().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("게시글 작성자만 삭제할 수 있습니다.");
        }

        postService.deletePost(boardId, postId);
        return ResponseEntity.noContent().build();
    }
}