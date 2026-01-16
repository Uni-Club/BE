package ycyh.uniclub.domain.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByBoard(Board board);

    Optional<Post> findByPostIdAndBoard_BoardId(Long postId, Long boardId);
}

