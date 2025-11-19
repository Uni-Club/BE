package ycyh.uniclub.domain.group;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupMemberDto {
    private Long memberId;
    private Long userId;
    private String userName;
    private String email;
    private String role;
    private String status;
    private Boolean isFinanceAdmin;
    private LocalDateTime joinedAt;

    public static GroupMemberDto from(GroupMember member) {
        return GroupMemberDto.builder()
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
