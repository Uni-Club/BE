package ycyh.uniclub.domain.recruitment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class RecruitmentResponseDto {
    private Long recruitmentId;
    private Long groupId;
    private Long schoolId;
    private String title;
    private String content;
    private String category;
    private List<String> tags;
    private LocalDateTime applyStart;
    private LocalDateTime applyEnd;
    private String status;
    private Integer capacity;
    private Integer views;
    private Boolean isUnion;
    private List<Object> customFields;

    public static RecruitmentResponseDto from(Recruitment entity) {
        return RecruitmentResponseDto.builder()
                .recruitmentId(entity.getRecruitmentId())
                .groupId(entity.getGroup() != null ? entity.getGroup().getGroupId() : null)
                .schoolId(entity.getSchool() != null ? entity.getSchool().getSchoolId() : null)
                .title(entity.getTitle())
                .content(entity.getContent())
                .category(entity.getCategory())
                .tags(parseTags(entity.getTags()))
                .applyStart(entity.getApplyStart())
                .applyEnd(entity.getApplyEnd())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .capacity(entity.getCapacity())
                .views(entity.getViews())
                .isUnion(entity.getIsUnion())
                .customFields(parseCustomFields(entity.getCustomFields()))
                .build();
    }
    
    private static List<Object> parseCustomFields(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(raw, new TypeReference<List<Object>>() {});
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    private static List<String> parseTags(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(raw, new TypeReference<List<String>>() {});
        } catch (Exception ignored) {
            // fallback: comma separated
            String[] parts = raw.split(",");
            List<String> result = new ArrayList<>();
            for (String p : parts) {
                String v = p.trim();
                if (!v.isEmpty()) {
                    result.add(v);
                }
            }
            return result;
        }
    }
}


