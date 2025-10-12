package ycyh.uniclub.domain.recruitment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RecruitmentUpdateDto {
    private String title;
    private String content;
    private String category;
    private List<String> tags;
    private LocalDateTime applyStart;
    private LocalDateTime applyEnd;
    private Integer capacity;
    private List<RecruitmentCreateDto.CustomFieldDto> customFields;
}
