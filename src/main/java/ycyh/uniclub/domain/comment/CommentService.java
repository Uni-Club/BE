package ycyh.uniclub.domain.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.board.Post;
import ycyh.uniclub.domain.board.PostRepository;
import ycyh.uniclub.domain.user.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 댓글 목록 조회
    public List<CommentResponseDto> getComments(Long boardId, Long postId) {
        getPostInBoard(boardId, postId);

        return commentRepository.findByPost_PostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(CommentResponseDto::from)
                .toList();
    }

    // 댓글 작성
    public CommentResponseDto createComment(Long boardId, Long postId, User user, CommentCreateRequestDto requestDto) {
        Post post = getPostInBoard(boardId, postId);

        Comment comment = Comment.builder()
                .post(post)
                .author(user)
                .content(requestDto.getContent())
                .build();

        Comment saved = commentRepository.save(comment);
        return CommentResponseDto.from(saved);
    }

    // 댓글 수정
    public CommentResponseDto updateComment(Long boardId, Long postId, Long commentId, User user, CommentUpdateRequestDto requestDto) {
        getPostInBoard(boardId, postId);

        Comment comment = commentRepository.findByCommentIdAndPost_PostId(commentId, postId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 권한 체크: 작성자만 수정 가능
        if (!comment.getAuthor().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("댓글 수정 권한이 없습니다.");
        }

        comment.updateContent(requestDto.getContent());
        return CommentResponseDto.from(comment);
    }

    // 댓글 삭제
    public void deleteComment(Long boardId, Long postId, Long commentId, User user) {
        getPostInBoard(boardId, postId);

        Comment comment = commentRepository.findByCommentIdAndPost_PostId(commentId, postId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 권한 체크: 작성자
        boolean isAuthor = comment.getAuthor().getUserId().equals(user.getUserId());

        if (!isAuthor) {
            throw new IllegalStateException("댓글 삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    // post가 board에 속하는지 검증하는 함수
    private Post getPostInBoard(Long boardId, Long postId) {
        return postRepository.findByPostIdAndBoard_BoardId(postId, boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }
}
