package ycyh.uniclub.domain.application;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ApplicationSubmitDto {
    private Long recruitmentId;
    private String motivation;
    private Map<String, Object> answers; // 커스텀 필드 답변
}
