package ycyh.uniclub.domain.me;

import lombok.Builder;
import lombok.Getter;
import ycyh.uniclub.domain.group.GroupMember;

@Getter
@Builder
public class MyGroupDto {
    private Long groupId;
    private String groupName;
    private Integer memberCount;
    private String role;

    public static MyGroupDto from(GroupMember gm) {
        return MyGroupDto.builder()
                .groupId(gm.getGroup().getGroupId())
                .groupName(gm.getGroup().getGroupName())
                .memberCount(gm.getGroup().getMembers() != null ? gm.getGroup().getMembers().size() : 0)
                .role(gm.getRole())
                .build();
    }
}


