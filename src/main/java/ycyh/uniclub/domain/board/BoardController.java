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
@RequestMapping("/api/clubs/{clubId}/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 그룹별 게시판 목록 조회
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getBoardsByClub(
            @PathVariable Long clubId,
            Authentication authentication
    ) {
        User user = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            user = (User) authentication.getPrincipal();
        }
        return ResponseEntity.ok(boardService.getBoardsByClub(clubId, user));
    }

    // 게시판 생성
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(
            @PathVariable Long clubId,
            @Valid @RequestBody BoardCreateRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.createBoard(clubId, request));
    }

    // 게시판 상세 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoardDetail(
            @PathVariable Long clubId,
            @PathVariable Long boardId
    ) {
        return ResponseEntity.ok(boardService.getBoardDetail(clubId, boardId));
    }

    // 게시판 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long clubId,
            @PathVariable Long boardId
    ) {
        boardService.deleteBoard(clubId, boardId);
        return ResponseEntity.noContent().build();
    }
}
