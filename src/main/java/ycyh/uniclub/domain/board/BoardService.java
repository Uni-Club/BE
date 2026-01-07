package ycyh.uniclub.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.group.Group;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;

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
}
