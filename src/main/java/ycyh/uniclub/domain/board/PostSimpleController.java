package ycyh.uniclub.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.user.User;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostSimpleController {

    private final PostService postService;

    // 게시글 상세 조회 (postId만으로)
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    // 게시글 수정 (postId만으로)
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @RequestBody PostUpdateRequestDto request
    ) {
        Post post = postService.getPostEntityById(postId);

        if (!post.getAuthor().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("게시글 작성자만 수정할 수 있습니다.");
        }

        return ResponseEntity.ok(postService.updatePostById(postId, request));
    }

    // 게시글 삭제 (postId만으로)
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user
    ) {
        Post post = postService.getPostEntityById(postId);

        if (!post.getAuthor().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("게시글 작성자만 삭제할 수 있습니다.");
        }

        postService.deletePostById(postId);
        return ResponseEntity.noContent().build();
    }
}
