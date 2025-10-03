package ycyh.uniclub.domain.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ycyh.uniclub.domain.group.entity.Group;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponseDto {
    private Long groupId;
    private String groupName;
    private String description;
    private Long leaderId;
    private String leaderName;
    private Long schoolId;
    private String schoolName;
    private Integer memberCount;
    private Integer activeRecruitmentCount;
    private LocalDateTime createdAt;
    
    public static GroupResponseDto from(Group group) {
        return GroupResponseDto.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .leaderId(group.getLeader() != null ? group.getLeader().getUserId() : null)
                .leaderName(group.getLeader() != null ? group.getLeader().getName() : null)
                .schoolId(group.getSchool() != null ? group.getSchool().getSchoolId() : null)
                .schoolName(group.getSchool() != null ? group.getSchool().getSchoolName() : null)
                .memberCount(group.getMembers() != null ? group.getMembers().size() : 0)
                .activeRecruitmentCount(group.getRecruitments() != null ? 
                        (int)group.getRecruitments().stream()
                                .filter(r -> "PUBLISHED".equals(r.getStatus().name()))
                                .count() : 0)
                .createdAt(group.getCreatedAt())
                .build();
    }
}

