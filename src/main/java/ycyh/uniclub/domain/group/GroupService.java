package ycyh.uniclub.domain.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.school.School;
import ycyh.uniclub.domain.school.SchoolRepository;
import ycyh.uniclub.domain.user.User;
import ycyh.uniclub.domain.user.UserRepository;
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
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    
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

    // 동아리 생성
    @Transactional
    public GroupResponseDto createGroup(GroupCreateDto dto, User user) {
        // 학교 조회 (선택사항)
        School school = null;
        if (dto.getSchoolId() != null) {
            school = schoolRepository.findById(dto.getSchoolId())
                    .orElseThrow(() -> new CustomException("학교를 찾을 수 없습니다"));
        }

        // 그룹 생성
        Group group = Group.builder()
                .groupName(dto.getGroupName())
                .description(dto.getDescription())
                .leader(user)
                .school(school)
                .isUnion(dto.getIsUnion() != null ? dto.getIsUnion() : false)
                .build();

        Group savedGroup = groupRepository.save(group);

        // 생성자를 회장으로 자동 추가
        GroupMember leaderMember = GroupMember.builder()
                .user(user)
                .group(savedGroup)
                .role("회장")
                .status("active")
                .build();

        groupMemberRepository.save(leaderMember);

        // 새로 생성된 그룹은 lazy 컬렉션이 초기화되지 않으므로 직접 빌드
        return GroupResponseDto.builder()
                .groupId(savedGroup.getGroupId())
                .groupName(savedGroup.getGroupName())
                .description(savedGroup.getDescription())
                .leaderId(user.getUserId())
                .leaderName(user.getName())
                .schoolId(school != null ? school.getSchoolId() : null)
                .schoolName(school != null ? school.getSchoolName() : null)
                .memberCount(1) // 생성자 1명
                .activeRecruitmentCount(0)
                .createdAt(savedGroup.getCreatedAt())
                .build();
    }

    // 동아리 수정
    @Transactional
    public GroupResponseDto updateGroup(Long groupId, GroupUpdateDto dto, User user) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));

        // 권한 체크: 관리자만 수정 가능
        if (!isGroupAdmin(user, group)) {
            throw new CustomException("동아리를 수정할 권한이 없습니다");
        }

        // 그룹 정보 업데이트를 위해 새 객체 생성 (엔티티에 setter가 없으므로)
        Group updatedGroup = Group.builder()
                .groupId(group.getGroupId())
                .groupName(dto.getGroupName() != null ? dto.getGroupName() : group.getGroupName())
                .description(dto.getDescription() != null ? dto.getDescription() : group.getDescription())
                .leader(group.getLeader())
                .school(group.getSchool())
                .createdAt(group.getCreatedAt())
                .isUnion(group.getIsUnion())
                .build();

        Group savedGroup = groupRepository.save(updatedGroup);

        // 업데이트 후 원래 그룹 정보를 기반으로 응답 빌드
        int memberCount = groupMemberRepository.findByGroupGroupId(groupId).size();
        return GroupResponseDto.builder()
                .groupId(savedGroup.getGroupId())
                .groupName(savedGroup.getGroupName())
                .description(savedGroup.getDescription())
                .leaderId(savedGroup.getLeader() != null ? savedGroup.getLeader().getUserId() : null)
                .leaderName(savedGroup.getLeader() != null ? savedGroup.getLeader().getName() : null)
                .schoolId(savedGroup.getSchool() != null ? savedGroup.getSchool().getSchoolId() : null)
                .schoolName(savedGroup.getSchool() != null ? savedGroup.getSchool().getSchoolName() : null)
                .memberCount(memberCount)
                .activeRecruitmentCount(0) // 업데이트 시 모집공고 수는 별도 조회 필요
                .createdAt(savedGroup.getCreatedAt())
                .build();
    }

    // 동아리 멤버 목록 조회
    public List<GroupMemberDto> getMembers(Long groupId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));

        return groupMemberRepository.findByGroupGroupId(groupId)
                .stream()
                .map(GroupMemberDto::from)
                .collect(Collectors.toList());
    }

    // 동아리 멤버 추가
    @Transactional
    public GroupMemberDto addMember(Long groupId, GroupMemberAddDto dto, User admin) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));

        // 권한 체크: 관리자만 멤버 추가 가능
        if (!isGroupAdmin(admin, group)) {
            throw new CustomException("멤버를 추가할 권한이 없습니다");
        }

        // 추가할 유저 조회
        User targetUser = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다"));

        // 이미 멤버인지 확인
        if (groupMemberRepository.existsByUserUserIdAndGroupGroupId(dto.getUserId(), groupId)) {
            throw new CustomException("이미 동아리에 가입된 멤버입니다");
        }

        // 멤버 추가
        GroupMember member = GroupMember.builder()
                .user(targetUser)
                .group(group)
                .role(dto.getRole() != null ? dto.getRole() : "부원")
                .status("active")
                .build();

        GroupMember savedMember = groupMemberRepository.save(member);
        return GroupMemberDto.from(savedMember);
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


