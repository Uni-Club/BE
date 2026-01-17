package ycyh.uniclub.domain.board;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardResponseDto {
    private Long boardId;
    private Long groupId;
    private String name;
    private String boardType;
    private String visibility;
    private LocalDateTime createdAt;
    private int postCount;

    public static BoardResponseDto from(Board board) {
        return BoardResponseDto.builder()
                .boardId(board.getBoardId())
                .groupId(board.getGroup().getGroupId())
                .name(board.getName())
                .boardType(board.getBoardType().name())
                .visibility(board.getVisibility().name())
                .createdAt(board.getCreatedAt())
                .postCount(board.getPosts() != null ? board.getPosts().size() : 0)
                .build();
    }
}
