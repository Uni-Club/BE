package ycyh.uniclub.domain.board;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCreateRequestDto {

    @NotBlank
    private String title;

    private String content;
}
