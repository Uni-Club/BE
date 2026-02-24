package ycyh.uniclub.domain.club;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ClubMemberDto {
    private Long memberId;
    private Long userId;
    private String userName;
    private String email;
    private String role;
    private String status;
    private Boolean isFinanceAdmin;
    private LocalDateTime joinedAt;

    public static ClubMemberDto from(ClubMember member) {
        return ClubMemberDto.builder()
                .memberId(member.getMemberId())
                .userId(member.getUser().getUserId())
                .userName(member.getUser().getName())
                .email(member.getUser().getEmail())
                .role(member.getRole())
                .status(member.getStatus())
                .isFinanceAdmin(member.getIsFinanceAdmin())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
