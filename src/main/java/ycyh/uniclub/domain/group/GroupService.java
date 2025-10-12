package ycyh.uniclub.domain.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.user.User;
import ycyh.uniclub.global.exception.CustomException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    
    public List<GroupResponseDto> searchGroups(GroupSearchDto searchDto) {
        List<Group> groups = groupRepository.searchGroups(
                searchDto.getKeyword(), 
                searchDto.getSchoolId()
        );
        
        return groups.stream()
                .map(GroupResponseDto::from)
                .collect(Collectors.toList());
    }
    
    public GroupResponseDto getGroupDetail(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));
        
        return GroupResponseDto.from(group);
    }
    
    public List<GroupResponseDto> getGroupsBySchool(Long schoolId) {
        List<Group> groups = groupRepository.findBySchoolSchoolId(schoolId);
        return groups.stream()
                .map(GroupResponseDto::from)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteGroup(Long groupId, User user) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));
        
        // 권한 체크: 그룹 리더만 삭제 가능
        if (group.getLeaderId() == null || !group.getLeaderId().equals(user.getUserId())) {
            throw new CustomException("동아리를 삭제할 권한이 없습니다. 동아리장만 삭제할 수 있습니다.");
        }
        
        // 그룹 삭제 (연관된 데이터도 CASCADE로 삭제됨)
        groupRepository.delete(group);
    }
    
    @Transactional
    public void requestLeave(Long groupId, User user, String reason) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));
        
        // 그룹 멤버십 확인
        groupMemberRepository.findByUserUserIdAndGroupGroupId(user.getUserId(), groupId)
                .orElseThrow(() -> new CustomException("해당 동아리의 멤버가 아닙니다"));
        
        // 동아리장은 탈퇴 요청할 수 없음
        if (group.getLeaderId() != null && group.getLeaderId().equals(user.getUserId())) {
            throw new CustomException("동아리장은 탈퇴할 수 없습니다. 다른 멤버에게 권한을 위임하거나 동아리를 삭제해주세요.");
        }
        
        // 이미 대기중인 요청이 있는지 확인
        if (leaveRequestRepository.existsByGroupGroupIdAndUserUserIdAndStatus(groupId, user.getUserId(), LeaveRequestStatus.PENDING)) {
            throw new CustomException("이미 탈퇴 요청이 진행중입니다");
        }
        
        // 탈퇴 요청 생성
        LeaveRequest request = LeaveRequest.builder()
                .group(group)
                .user(user)
                .reason(reason)
                .status(LeaveRequestStatus.PENDING)
                .build();
        
        leaveRequestRepository.save(request);
    }
    
    @Transactional
    public void approveLeaveRequest(Long requestId, User reviewer, String reviewNote) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("탈퇴 요청을 찾을 수 없습니다"));
        
        // 권한 체크
        if (!isGroupAdmin(reviewer, request.getGroup())) {
            throw new CustomException("탈퇴 요청을 승인할 권한이 없습니다");
        }
        
        // 상태 업데이트
        request.setStatus(LeaveRequestStatus.APPROVED);
        request.setReviewer(reviewer);
        request.setReviewNote(reviewNote);
        request.setReviewedAt(java.time.LocalDateTime.now());
        
        // 멤버십 삭제
        GroupMember membership = groupMemberRepository.findByUserUserIdAndGroupGroupId(
                request.getUser().getUserId(), request.getGroup().getGroupId())
                .orElse(null);
        
        if (membership != null) {
            groupMemberRepository.delete(membership);
        }
    }
    
    @Transactional
    public void rejectLeaveRequest(Long requestId, User reviewer, String reviewNote) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("탈퇴 요청을 찾을 수 없습니다"));
        
        // 권한 체크
        if (!isGroupAdmin(reviewer, request.getGroup())) {
            throw new CustomException("탈퇴 요청을 거절할 권한이 없습니다");
        }
        
        // 상태 업데이트
        request.setStatus(LeaveRequestStatus.REJECTED);
        request.setReviewer(reviewer);
        request.setReviewNote(reviewNote);
        request.setReviewedAt(java.time.LocalDateTime.now());
    }
    
    @Transactional
    public void removeMember(Long groupId, Long userId, User admin) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));
        
        // 권한 체크
        if (!isGroupAdmin(admin, group)) {
            throw new CustomException("멤버를 강제 탈퇴시킬 권한이 없습니다");
        }
        
        // 자기 자신은 강제 탈퇴 불가
        if (admin.getUserId().equals(userId)) {
            throw new CustomException("자기 자신을 강제 탈퇴시킬 수 없습니다");
        }
        
        // 동아리장은 강제 탈퇴 불가
        if (group.getLeaderId().equals(userId)) {
            throw new CustomException("동아리장을 강제 탈퇴시킬 수 없습니다");
        }
        
        // 멤버십 삭제
        GroupMember membership = groupMemberRepository.findByUserUserIdAndGroupGroupId(userId, groupId)
                .orElseThrow(() -> new CustomException("해당 멤버를 찾을 수 없습니다"));
        
        groupMemberRepository.delete(membership);
    }
    
    public List<LeaveRequestDto> getLeaveRequests(Long groupId, User user) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));
        
        // 권한 체크
        if (!isGroupAdmin(user, group)) {
            throw new CustomException("탈퇴 요청을 조회할 권한이 없습니다");
        }
        
        return leaveRequestRepository.findByGroupGroupIdAndStatus(groupId, LeaveRequestStatus.PENDING)
                .stream()
                .map(LeaveRequestDto::from)
                .collect(Collectors.toList());
    }
    
    public LeaveRequestDto getMyLeaveRequest(Long groupId, User user) {
        return leaveRequestRepository.findByGroupGroupIdAndUserUserIdAndStatus(
                groupId, user.getUserId(), LeaveRequestStatus.PENDING)
                .map(LeaveRequestDto::from)
                .orElse(null);
    }
    
    private boolean isGroupAdmin(User user, Group group) {
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
}


