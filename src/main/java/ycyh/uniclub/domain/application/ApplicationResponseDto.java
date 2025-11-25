package ycyh.uniclub.domain.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class ApplicationResponseDto {
    private Long applicationId;
    private Long recruitmentId;
    private String recruitmentTitle;
    private Long groupId;
    private String groupName;
    private Long applicantId;
    private String applicantName;
    private String applicantEmail;
    private ApplicationStatus status;
    private String motivation;
    private Map<String, Object> answers;
    private String reviewerName;
    private String reviewNote;
    private LocalDateTime appliedAt;
    private LocalDateTime decidedAt;
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public static ApplicationResponseDto from(Application entity) {
        return ApplicationResponseDto.builder()
                .applicationId(entity.getApplicationId())
                .recruitmentId(entity.getRecruitment().getRecruitmentId())
                .recruitmentTitle(entity.getRecruitment().getTitle())
                .groupId(entity.getGroup().getGroupId())
                .groupName(entity.getGroup().getGroupName())
                .applicantId(entity.getApplicant().getUserId())
                .applicantName(entity.getApplicant().getName())
                .applicantEmail(entity.getApplicant().getEmail())
                .status(entity.getStatus())
                .motivation(entity.getMotivation())
                .answers(parseAnswers(entity.getAnswers()))
                .reviewerName(entity.getReviewer() != null ? entity.getReviewer().getName() : null)
                .reviewNote(entity.getReviewNote())
                .appliedAt(entity.getAppliedAt())
                .decidedAt(entity.getDecidedAt())
                .build();
    }
    
    private static Map<String, Object> parseAnswers(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
