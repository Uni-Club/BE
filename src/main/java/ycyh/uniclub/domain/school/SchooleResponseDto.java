package ycyh.uniclub.domain.school;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SchooleResponseDto {
    private Long schoolId;
    private String schoolName;
    private String campusName;
    private String region;
    private String domain;
    private LocalDateTime createdAt;

    public static SchooleResponseDto from(School s) {
        return new SchooleResponseDto(
                s.getSchoolId(),
                s.getSchoolName(),
                s.getCampusName(),
                s.getRegion(),
                s.getDomain(),
                s.getCreatedAt()
        );
    }
}
