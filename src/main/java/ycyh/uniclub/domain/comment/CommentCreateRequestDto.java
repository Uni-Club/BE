package ycyh.uniclub.domain.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommentCreateRequestDto {

    @NotBlank
    @Size(max = 2000)
    private String content;
}
