package ycyh.uniclub.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.club.Club;
import ycyh.uniclub.domain.club.ClubAuthorizationService;
import ycyh.uniclub.domain.club.ClubRepository;
import ycyh.uniclub.domain.user.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final ClubRepository clubRepository;
    private final ClubAuthorizationService clubAuthorizationService;

    // 기본 게시판 자동 생성
    public void createDefaultBoardsforClub(Club club) {
        createBoard(club, "공지사항", BoardType.NOTICE, PostVisibility.CLUB_ONLY);
        createBoard(club, "자유게시판", BoardType.FREE, PostVisibility.CLUB_ONLY);
        createBoard(club, "질문답변", BoardType.QNA, PostVisibility.CLUB_ONLY);
    }

    public Board createBoard(Club club, String name, BoardType type, PostVisibility visibility) {
        Board board = Board.builder()
                .club(club)
                .name(name)
                .boardType(type)
                .visibility(visibility)
                .build();

        return boardRepository.save(board);
    }

    // 그룹별 게시판 목록 조회
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getBoardsByClub(Long clubId, User user) {
        clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        boolean isMember = user != null && clubAuthorizationService.isClubMember(user, clubId);

        return boardRepository.findByClub_ClubIdOrderByCreatedAtAsc(clubId)
                .stream()
                .filter(board -> board.getVisibility() != PostVisibility.CLUB_ONLY || isMember)
                .map(BoardResponseDto::from)
                .toList();
    }

    // 게시판 생성
    public BoardResponseDto createBoard(Long clubId, BoardCreateRequestDto request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        BoardType boardType = request.getBoardType() != null ? request.getBoardType() : BoardType.FREE;
        PostVisibility visibility = request.getVisibility() != null ? request.getVisibility() : PostVisibility.CLUB_ONLY;

        Board board = createBoard(club, request.getName(), boardType, visibility);
        return BoardResponseDto.from(board);
    }

    // 게시판 상세 조회
    @Transactional(readOnly = true)
    public BoardResponseDto getBoardDetail(Long clubId, Long boardId) {
        Board board = boardRepository.findByBoardIdAndClub_ClubId(boardId, clubId)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다."));

        return BoardResponseDto.from(board);
    }

    // 게시판 삭제
    public void deleteBoard(Long clubId, Long boardId) {
        Board board = boardRepository.findByBoardIdAndClub_ClubId(boardId, clubId)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다."));

        boardRepository.delete(board);
    }
}
