package ycyh.uniclub.domain.board;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.user.User;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 그룹별 게시판 목록 조회
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getBoardsByGroup(
            @PathVariable Long groupId,
            Authentication authentication
    ) {
        User user = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            user = (User) authentication.getPrincipal();
        }
        return ResponseEntity.ok(boardService.getBoardsByGroup(groupId, user));
    }

    // 게시판 생성
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(
            @PathVariable Long groupId,
            @Valid @RequestBody BoardCreateRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.createBoard(groupId, request));
    }

    // 게시판 상세 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoardDetail(
            @PathVariable Long groupId,
            @PathVariable Long boardId
    ) {
        return ResponseEntity.ok(boardService.getBoardDetail(groupId, boardId));
    }

    // 게시판 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long groupId,
            @PathVariable Long boardId
    ) {
        boardService.deleteBoard(groupId, boardId);
        return ResponseEntity.noContent().build();
    }
}
