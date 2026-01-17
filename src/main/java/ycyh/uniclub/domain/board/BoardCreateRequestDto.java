package ycyh.uniclub.domain.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class BoardCreateRequestDto {
    @NotBlank
    @Size(max = 100)
    private String name;

    private BoardType boardType;

    private PostVisibility visibility;
}
