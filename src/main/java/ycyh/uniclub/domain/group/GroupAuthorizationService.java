package ycyh.uniclub.domain.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.user.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupAuthorizationService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public boolean isGroupAdmin(User user, Group group) {
        // 그룹 리더인지 확인
        if (group.getLeaderId() != null && group.getLeaderId().equals(user.getUserId())) {
            return true;
        }

        // 그룹 멤버 중 관리자 권한이 있는지 확인
        return groupMemberRepository.findByUserUserIdAndGroupGroupId(user.getUserId(), group.getGroupId())
                .map(member -> {
                    String role = member.getRole();
                    return "회장".equals(role) || "부회장".equals(role) || "관리자".equals(role);
                })
                .orElse(false);
    }

    public boolean isGroupAdmin(User user, Long groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return false;
        }
        return isGroupAdmin(user, group);
    }

    public boolean isGroupMember(User user, Long groupId) {
        return groupMemberRepository.existsByUserUserIdAndGroupGroupId(user.getUserId(), groupId);
    }

    public Optional<String> getRole(User user, Long groupId) {
        return groupMemberRepository.findByUserUserIdAndGroupGroupId(user.getUserId(), groupId)
                .map(GroupMember::getRole);
    }
}
