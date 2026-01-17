package ycyh.uniclub.domain.board;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;

    // 특정 게시판의 게시글 목록 조회
    public List<PostResponseDto> getPostsByBoard(Long boardID) {
        Board board = boardRepository.findById(boardID)
                .orElseThrow(() -> new EntityNotFoundException("게시판을 찾을 수 없습니다. id=" + boardID));

        List<Post> posts = postRepository.findByBoard(board);

        return posts.stream()
                .map(this::toDto)
                .toList();
    }

    // 게시글 생성
    public PostResponseDto createPost(Long boardId, PostCreateRequestDto req, User author) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시판을 찾을 수 없습니다. id=" + boardId));

        Post post = Post.builder()
                .board(board)
                .group(board.getGroup())
                .author(author)
                .title(req.getTitle())
                .content(req.getContent())
                // isNoticed, isPinned, createdAt은 엔티티의 @Builder.default로 처리
                .build();

        Post saved = postRepository.save(post);
        return toDto(saved);
    }

    // 게시글 상세 조회
    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long boardId, Long postId) {
        Post post = getPostEntity(boardId, postId);

        return toDto((post));
    }

    // 게시글 수정
    public PostResponseDto updatePost(Long boardId, Long postId, PostUpdateRequestDto req) {
        Post post = getPostEntity(boardId, postId);

        if (req.getTitle() != null) {
            String trimmed = req.getTitle().trim();
            if (!trimmed.isEmpty()) {
                post.setTitle(trimmed);
            }
        }

        if (req.getContent() != null) {
            post.setContent(req.getContent());
        }

        post.setUpdatedAt(LocalDateTime.now());

        return toDto(post);
    }

    //게시글 삭제
    public void deletePost(Long boardId, Long postId) {
        Post post = getPostEntity(boardId, postId);

        postRepository.delete(post);
    }

    // 작성자 조회용 메서드
    @Transactional(readOnly = true)
    public Post getPostEntity(Long boardId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. id=" + postId));

        // postId가 boardId 소속인지 검증
        if (!post.getBoard().getBoardId().equals(boardId)) {
            throw new EntityNotFoundException("해당 게시판에 속한 게시글이 아닙니다. boardId=" + boardId);
        }

        return post;
    }

    // postId만으로 게시글 조회 (FE 호환용)
    @Transactional(readOnly = true)
    public Post getPostEntityById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. id=" + postId));
    }

    // postId만으로 게시글 상세 조회 (FE 호환용)
    @Transactional(readOnly = true)
    public PostResponseDto getPostById(Long postId) {
        Post post = getPostEntityById(postId);
        return toDto(post);
    }

    // postId만으로 게시글 수정 (FE 호환용)
    public PostResponseDto updatePostById(Long postId, PostUpdateRequestDto req) {
        Post post = getPostEntityById(postId);

        if (req.getTitle() != null) {
            String trimmed = req.getTitle().trim();
            if (!trimmed.isEmpty()) {
                post.setTitle(trimmed);
            }
        }

        if (req.getContent() != null) {
            post.setContent(req.getContent());
        }

        post.setUpdatedAt(LocalDateTime.now());

        return toDto(post);
    }

    // postId만으로 게시글 삭제 (FE 호환용)
    public void deletePostById(Long postId) {
        Post post = getPostEntityById(postId);
        postRepository.delete(post);
    }

    // ============= 내부 변환 메서드 =============
    private PostResponseDto toDto(Post post) {
        return PostResponseDto.builder()
                .postId(post.getPostId())
                .boardId(post.getBoard().getBoardId())
                .groupId(post.getGroup().getGroupId())
                .authorId(post.getAuthor().getUserId())
                .authorName(post.getAuthor().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .isNotice(post.getIsNotice())
                .isPinned(post.getIsPinned())
                .pinnedUntil(post.getPinnedUntil())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
