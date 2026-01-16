package ycyh.uniclub.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost_PostIdOrderByCreatedAtAsc(Long postId);

    Optional<Comment> findByCommentIdAndPost_PostId(Long commentId, Long postId);

    void deleteByCommentIdAndPost_PostId(Long commentId, Long PostId);
}
