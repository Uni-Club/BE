package ycyh.uniclub.domain.recruitment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RecruitmentCreateDto {
    private Long clubId;
    private String title;
    private String content;
    private String category;
    private List<String> tags;
    private LocalDateTime applyStart;
    private LocalDateTime applyEnd;
    private Integer capacity;
    private List<CustomFieldDto> customFields;
    
    @Getter
    @Setter
    public static class CustomFieldDto {
        private String fieldName;
        private String fieldType; // text, number, textarea, select
        private Boolean required;
        private String placeholder;
        private List<String> options; // for select type
    }
}
