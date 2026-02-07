package ycyh.uniclub.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.group.Group;
import ycyh.uniclub.domain.group.GroupAuthorizationService;
import ycyh.uniclub.domain.group.GroupRepository;
import ycyh.uniclub.domain.user.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final GroupRepository groupRepository;
    private final GroupAuthorizationService groupAuthorizationService;

    // 기본 게시판 자동 생성
    public void createDefaultBoardsforGroup(Group group) {
        createBoard(group, "공지사항", BoardType.NOTICE, PostVisibility.GROUP_ONLY);
        createBoard(group, "자유게시판", BoardType.FREE, PostVisibility.GROUP_ONLY);
        createBoard(group, "질문답변", BoardType.QNA, PostVisibility.GROUP_ONLY);
    }

    public Board createBoard(Group group, String name, BoardType type, PostVisibility visibility) {
        Board board = Board.builder()
                .group(group)
                .name(name)
                .boardType(type)
                .visibility(visibility)
                .build();

        return boardRepository.save(board);
    }

    // 그룹별 게시판 목록 조회
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getBoardsByGroup(Long groupId, User user) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        boolean isMember = user != null && groupAuthorizationService.isGroupMember(user, groupId);

        return boardRepository.findByGroup_GroupIdOrderByCreatedAtAsc(groupId)
                .stream()
                .filter(board -> board.getVisibility() != PostVisibility.GROUP_ONLY || isMember)
                .map(BoardResponseDto::from)
                .toList();
    }

    // 게시판 생성
    public BoardResponseDto createBoard(Long groupId, BoardCreateRequestDto request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다."));

        BoardType boardType = request.getBoardType() != null ? request.getBoardType() : BoardType.FREE;
        PostVisibility visibility = request.getVisibility() != null ? request.getVisibility() : PostVisibility.GROUP_ONLY;

        Board board = createBoard(group, request.getName(), boardType, visibility);
        return BoardResponseDto.from(board);
    }

    // 게시판 상세 조회
    @Transactional(readOnly = true)
    public BoardResponseDto getBoardDetail(Long groupId, Long boardId) {
        Board board = boardRepository.findByBoardIdAndGroup_GroupId(boardId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다."));

        return BoardResponseDto.from(board);
    }

    // 게시판 삭제
    public void deleteBoard(Long groupId, Long boardId) {
        Board board = boardRepository.findByBoardIdAndGroup_GroupId(boardId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다."));

        boardRepository.delete(board);
    }
}
