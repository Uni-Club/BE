package ycyh.uniclub.domain.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {

    private Long postId;

    private Long boardId;
    private Long groupId;

    private Long authorId;
    private String authorName;

    private String title;
    private String content;

    private Boolean isNotice;
    private Boolean isPinned;
    private LocalDateTime pinnedUntil;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
