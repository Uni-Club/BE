package ycyh.uniclub.domain.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.board.BoardService;
import ycyh.uniclub.domain.notification.NotificationService;
import ycyh.uniclub.domain.notification.NotificationType;
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
    private final BoardService boardService;
    private final NotificationService notificationService;

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
        School school = schoolRepository.findById(dto.getSchoolId())
                .orElseThrow(() -> new CustomException("학교를 찾을 수 없습니다"));

        // 같은 학교에 동일 이름의 동아리가 있는지 확인
        if (groupRepository.existsByGroupNameAndSchoolSchoolId(dto.getGroupName(), dto.getSchoolId())) {
            throw new CustomException("같은 학교에 동일한 이름의 동아리가 이미 존재합니다");
        }

        Group group = Group.builder()
                .groupName(dto.getGroupName())
                .description(dto.getDescription())
                .school(school)
                .leader(user)
                .isUnion(dto.getIsUnion() != null ? dto.getIsUnion() : false)
                .build();

        Group savedGroup = groupRepository.save(group);

        // 생성자를 회장으로 멤버에 추가
        GroupMember member = GroupMember.builder()
                .user(user)
                .group(savedGroup)
                .role("회장")
                .build();

        // 그룹 생성시 기본 게시판 자동 생성
        boardService.createDefaultBoardsforGroup(savedGroup);

        groupMemberRepository.save(member);

        return GroupResponseDto.from(savedGroup);
    }

    // 동아리 수정
    @Transactional
    public GroupResponseDto updateGroup(Long groupId, GroupUpdateDto dto, User user) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));

        // 권한 체크: 회장만 수정 가능
        if (group.getLeaderId() == null || !group.getLeaderId().equals(user.getUserId())) {
            throw new CustomException("동아리를 수정할 권한이 없습니다. 동아리장만 수정할 수 있습니다.");
        }

        // Group Entity에 setter가 없으므로 새 객체 생성하여 저장
        Group updatedGroup = Group.builder()
                .groupId(group.getGroupId())
                .groupName(dto.getGroupName() != null ? dto.getGroupName() : group.getGroupName())
                .description(dto.getDescription() != null ? dto.getDescription() : group.getDescription())
                .school(group.getSchool())
                .leader(group.getLeader())
                .isUnion(group.getIsUnion())
                .createdAt(group.getCreatedAt())
                .build();

        Group savedGroup = groupRepository.save(updatedGroup);
        return GroupResponseDto.from(savedGroup);
    }

    // 멤버 목록 조회
    public List<GroupMemberDto> getMembers(Long groupId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));

        return groupMemberRepository.findByGroupGroupId(groupId)
                .stream()
                .map(GroupMemberDto::from)
                .collect(Collectors.toList());
    }

    // 멤버 추가
    @Transactional
    public GroupMemberDto addMember(Long groupId, GroupMemberAddDto dto, User admin) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));

        // 권한 체크
        if (!isGroupAdmin(admin, group)) {
            throw new CustomException("멤버를 추가할 권한이 없습니다");
        }

        User userToAdd = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다"));

        // 이미 멤버인지 확인
        if (groupMemberRepository.findByUserUserIdAndGroupGroupId(dto.getUserId(), groupId).isPresent()) {
            throw new CustomException("이미 동아리 멤버입니다");
        }

        GroupMember member = GroupMember.builder()
                .user(userToAdd)
                .group(group)
                .role(dto.getRole() != null ? dto.getRole() : "부원")
                .build();

        GroupMember savedMember = groupMemberRepository.save(member);

        // 알림 생성 (멤버 승인/추가)
        String content = String.format("'%s' 동아리 가입이 승인되었습니다.", group.getGroupName());
        String relatedUrl = String.format("/api/groups/%d", group.getGroupId());
        notificationService.create(userToAdd, NotificationType.MEMBER_APPROVED, content, relatedUrl);

        return GroupMemberDto.from(savedMember);
    }

    // 탈퇴 요청 처리 (승인/거절 통합)
    @Transactional
    public LeaveRequestDto processLeaveRequest(Long groupId, Long requestId, LeaveRequestReviewDto dto, User reviewer) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("탈퇴 요청을 찾을 수 없습니다"));

        // groupId 검증
        if (!request.getGroup().getGroupId().equals(groupId)) {
            throw new CustomException("해당 동아리의 탈퇴 요청이 아닙니다");
        }

        // 권한 체크
        if (!isGroupAdmin(reviewer, request.getGroup())) {
            throw new CustomException("탈퇴 요청을 처리할 권한이 없습니다");
        }

        // 이미 처리된 요청인지 확인
        if (request.getStatus() != LeaveRequestStatus.PENDING) {
            throw new CustomException("이미 처리된 탈퇴 요청입니다");
        }

        String action = dto.getAction();
        if ("APPROVE".equalsIgnoreCase(action)) {
            request.setStatus(LeaveRequestStatus.APPROVED);

            // 멤버십 삭제
            GroupMember membership = groupMemberRepository.findByUserUserIdAndGroupGroupId(
                    request.getUser().getUserId(), request.getGroup().getGroupId())
                    .orElse(null);

            if (membership != null) {
                groupMemberRepository.delete(membership);
            }
        } else if ("REJECT".equalsIgnoreCase(action)) {
            request.setStatus(LeaveRequestStatus.REJECTED);
        } else {
            throw new CustomException("유효하지 않은 action입니다. APPROVE 또는 REJECT를 사용하세요.");
        }

        request.setReviewer(reviewer);
        request.setReviewNote(dto.getReviewNote());
        request.setReviewedAt(java.time.LocalDateTime.now());

        return LeaveRequestDto.from(request);
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


