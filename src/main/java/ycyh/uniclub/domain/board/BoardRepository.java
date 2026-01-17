package ycyh.uniclub.domain.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByGroup_GroupIdOrderByCreatedAtAsc(Long groupId);
    Optional<Board> findByBoardIdAndGroup_GroupId(Long boardId, Long groupId);
}
