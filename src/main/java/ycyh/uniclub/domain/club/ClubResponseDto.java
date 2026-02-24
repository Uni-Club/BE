package ycyh.uniclub.domain.club;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubResponseDto {
    private Long clubId;
    private String clubName;
    private String description;
    private Long leaderId;
    private String leaderName;
    private Long schoolId;
    private String schoolName;
    private Integer memberCount;
    private Integer activeRecruitmentCount;
    private LocalDateTime createdAt;
    
    public static ClubResponseDto from(Club club) {
        return ClubResponseDto.builder()
                .clubId(club.getClubId())
                .clubName(club.getClubName())
                .description(club.getDescription())
                .leaderId(club.getLeader() != null ? club.getLeader().getUserId() : null)
                .leaderName(club.getLeader() != null ? club.getLeader().getName() : null)
                .schoolId(club.getSchool() != null ? club.getSchool().getSchoolId() : null)
                .schoolName(club.getSchool() != null ? club.getSchool().getSchoolName() : null)
                .memberCount(club.getMembers() != null ? club.getMembers().size() : 0)
                .activeRecruitmentCount(club.getRecruitments() != null ?
                        (int) club.getRecruitments().stream()
                                .filter(r -> r.getStatus() != null && r.getStatus().name().equals("PUBLISHED"))
                                .count() : 0)
                .createdAt(club.getCreatedAt())
                .build();
    }
}


