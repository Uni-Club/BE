package ycyh.uniclub.domain.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByClub_ClubIdOrderByCreatedAtAsc(Long clubId);
    Optional<Board> findByBoardIdAndClub_ClubId(Long boardId, Long clubId);
}
