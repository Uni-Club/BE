package ycyh.uniclub.domain.club;

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
public class ClubService {
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final BoardService boardService;
    private final ClubAuthorizationService clubAuthorizationService;
    private final NotificationService notificationService;

    public List<ClubResponseDto> searchClubs(ClubSearchDto searchDto) {
        List<Club> clubs = clubRepository.searchClubs(
                searchDto.getKeyword(),
                searchDto.getSchoolId()
        );

        return clubs.stream()
                .map(ClubResponseDto::from)
                .collect(Collectors.toList());
    }

    public ClubResponseDto getClubDetail(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));

        return ClubResponseDto.from(club);
    }

    public List<ClubResponseDto> getClubsBySchool(Long schoolId) {
        List<Club> clubs = clubRepository.findBySchoolSchoolId(schoolId);
        return clubs.stream()
                .map(ClubResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteClub(Long clubId, User user) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));

        // 권한 체크: 그룹 리더만 삭제 가능
        if (club.getLeaderId() == null || !club.getLeaderId().equals(user.getUserId())) {
            throw new CustomException("동아리를 삭제할 권한이 없습니다. 동아리장만 삭제할 수 있습니다.");
        }

        // 그룹 삭제 (연관된 데이터도 CASCADE로 삭제됨)
        clubRepository.delete(club);
    }

    // 탈퇴 요청 생성
    @Transactional
    public void requestLeave(Long clubId, User user, String reason) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));

        // 그룹 멤버십 확인
        clubMemberRepository.findByUserUserIdAndClubClubId(user.getUserId(), clubId)
                .orElseThrow(() -> new CustomException("해당 동아리의 멤버가 아닙니다"));

        // 동아리장은 탈퇴 요청할 수 없음
        if (club.getLeaderId() != null && club.getLeaderId().equals(user.getUserId())) {
            throw new CustomException("동아리장은 탈퇴할 수 없습니다. 다른 멤버에게 권한을 위임하거나 동아리를 삭제해주세요.");
        }

        // 이미 대기중인 요청이 있는지 확인
        if (leaveRequestRepository.existsByClubClubIdAndUserUserIdAndStatus(clubId, user.getUserId(), LeaveRequestStatus.PENDING)) {
            throw new CustomException("이미 탈퇴 요청이 진행중입니다");
        }

        // 탈퇴 요청 생성
        LeaveRequest request = LeaveRequest.builder()
                .club(club)
                .user(user)
                .reason(reason)
                .status(LeaveRequestStatus.PENDING)
                .build();

        leaveRequestRepository.save(request);

        // 알림 트리거: 회장(leader)에게 "탈퇴 신청" 알림
        if (club.getLeaderId() != null) {
            User leader = userRepository.findById(club.getLeaderId())
                    .orElseThrow(() -> new CustomException("동아리장을 찾을 수 없습니다."));

            String content = String.format("'%s'님이 '%s' 동아리 탈퇴를 신청했습니다.", user.getName(), club.getClubName());
            String relatedUrl = String.format("/api/clubs/%d/leave-requests", clubId);

            notificationService.create(leader, NotificationType.LEAVE_REQUESTED, content, relatedUrl);
        }
    }

    @Transactional
    public void approveLeaveRequest(Long requestId, User reviewer, String reviewNote) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("탈퇴 요청을 찾을 수 없습니다"));

        // 권한 체크
        if (!clubAuthorizationService.isClubAdmin(reviewer, request.getClub())) {
            throw new CustomException("탈퇴 요청을 승인할 권한이 없습니다");
        }

        // 상태 업데이트
        request.setStatus(LeaveRequestStatus.APPROVED);
        request.setReviewer(reviewer);
        request.setReviewNote(reviewNote);
        request.setReviewedAt(java.time.LocalDateTime.now());

        // 멤버십 삭제
        ClubMember membership = clubMemberRepository.findByUserUserIdAndClubClubId(
                request.getUser().getUserId(), request.getClub().getClubId())
                .orElse(null);

        if (membership != null) {
            clubMemberRepository.delete(membership);
        }
    }

    @Transactional
    public void rejectLeaveRequest(Long requestId, User reviewer, String reviewNote) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("탈퇴 요청을 찾을 수 없습니다"));

        // 권한 체크
        if (!clubAuthorizationService.isClubAdmin(reviewer, request.getClub())) {
            throw new CustomException("탈퇴 요청을 거절할 권한이 없습니다");
        }

        // 상태 업데이트
        request.setStatus(LeaveRequestStatus.REJECTED);
        request.setReviewer(reviewer);
        request.setReviewNote(reviewNote);
        request.setReviewedAt(java.time.LocalDateTime.now());
    }

    // 멤버 삭제
    @Transactional
    public void removeMember(Long clubId, Long userId, User admin) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));

        // 권한 체크
        if (!clubAuthorizationService.isClubAdmin(admin, club)) {
            throw new CustomException("멤버를 강제 탈퇴시킬 권한이 없습니다");
        }

        // 자기 자신은 강제 탈퇴 불가
        if (admin.getUserId().equals(userId)) {
            throw new CustomException("자기 자신을 강제 탈퇴시킬 수 없습니다");
        }

        // 동아리장은 강제 탈퇴 불가
        if (club.getLeaderId().equals(userId)) {
            throw new CustomException("동아리장을 강제 탈퇴시킬 수 없습니다");
        }

        // 퇴출 대상 사용자 조회 (알림 receiver)
        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        // 멤버십 삭제
        ClubMember membership = clubMemberRepository.findByUserUserIdAndClubClubId(userId, clubId)
                .orElseThrow(() -> new CustomException("해당 멤버를 찾을 수 없습니다"));

        clubMemberRepository.delete(membership);

        // 알림 트리거: 강제 퇴출
        String content = String.format("'%s' 동아리에서 퇴출되었습니다.", club.getClubName());
        notificationService.create(userToRemove, NotificationType.MEMBER_KICKED, content, null);
    }

    public List<LeaveRequestDto> getLeaveRequests(Long clubId, User user) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));

        // 권한 체크
        if (!clubAuthorizationService.isClubAdmin(user, club)) {
            throw new CustomException("탈퇴 요청을 조회할 권한이 없습니다");
        }

        return leaveRequestRepository.findByClubClubIdAndStatus(clubId, LeaveRequestStatus.PENDING)
                .stream()
                .map(LeaveRequestDto::from)
                .collect(Collectors.toList());
    }

    public LeaveRequestDto getMyLeaveRequest(Long clubId, User user) {
        return leaveRequestRepository.findByClubClubIdAndUserUserIdAndStatus(
                clubId, user.getUserId(), LeaveRequestStatus.PENDING)
                .map(LeaveRequestDto::from)
                .orElse(null);
    }

    // 동아리 생성
    @Transactional
    public ClubResponseDto createClub(ClubCreateDto dto, User user) {
        School school = schoolRepository.findById(dto.getSchoolId())
                .orElseThrow(() -> new CustomException("학교를 찾을 수 없습니다"));

        // 같은 학교에 동일 이름의 동아리가 있는지 확인
        if (clubRepository.existsByClubNameAndSchoolSchoolId(dto.getClubName(), dto.getSchoolId())) {
            throw new CustomException("같은 학교에 동일한 이름의 동아리가 이미 존재합니다");
        }

        Club club = Club.builder()
                .clubName(dto.getClubName())
                .description(dto.getDescription())
                .school(school)
                .leader(user)
                .isUnion(dto.getIsUnion() != null ? dto.getIsUnion() : false)
                .build();

        Club savedClub = clubRepository.save(club);

        // 생성자를 회장으로 멤버에 추가
        ClubMember member = ClubMember.builder()
                .user(user)
                .club(savedClub)
                .role("회장")
                .build();

        // 그룹 생성시 기본 게시판 자동 생성
        boardService.createDefaultBoardsforClub(savedClub);

        clubMemberRepository.save(member);

        return ClubResponseDto.from(savedClub);
    }

    // 동아리 수정
    @Transactional
    public ClubResponseDto updateClub(Long clubId, ClubUpdateDto dto, User user) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));

        // 권한 체크: 회장만 수정 가능
        if (club.getLeaderId() == null || !club.getLeaderId().equals(user.getUserId())) {
            throw new CustomException("동아리를 수정할 권한이 없습니다. 동아리장만 수정할 수 있습니다.");
        }

        // Update fields in-place to preserve collection relationships (members, boards, recruitments, schedules)
        club.updateInfo(dto.getClubName(), dto.getDescription());

        return ClubResponseDto.from(club);
    }

    // 멤버 목록 조회
    public List<ClubMemberDto> getMembers(Long clubId) {
        clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));

        return clubMemberRepository.findByClubClubId(clubId)
                .stream()
                .map(ClubMemberDto::from)
                .collect(Collectors.toList());
    }

    // 멤버 추가
    @Transactional
    public ClubMemberDto addMember(Long clubId, ClubMemberAddDto dto, User admin) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));

        // 권한 체크
        if (!clubAuthorizationService.isClubAdmin(admin, club)) {
            throw new CustomException("멤버를 추가할 권한이 없습니다");
        }

        User userToAdd = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다"));

        // 이미 멤버인지 확인
        if (clubMemberRepository.findByUserUserIdAndClubClubId(dto.getUserId(), clubId).isPresent()) {
            throw new CustomException("이미 동아리 멤버입니다");
        }

        ClubMember member = ClubMember.builder()
                .user(userToAdd)
                .club(club)
                .role(dto.getRole() != null ? dto.getRole() : "부원")
                .build();

        ClubMember savedMember = clubMemberRepository.save(member);

        // 알림 생성 (멤버 승인/추가)
        String content = String.format("'%s' 동아리 가입이 승인되었습니다.", club.getClubName());
        String relatedUrl = String.format("/api/clubs/%d", club.getClubId());
        notificationService.create(userToAdd, NotificationType.MEMBER_APPROVED, content, relatedUrl);

        return ClubMemberDto.from(savedMember);
    }

    // 탈퇴 요청 처리 (승인/거절 통합)
    @Transactional
    public LeaveRequestDto processLeaveRequest(Long clubId, Long requestId, LeaveRequestReviewDto dto, User reviewer) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("탈퇴 요청을 찾을 수 없습니다"));

        // clubId 검증
        if (!request.getClub().getClubId().equals(clubId)) {
            throw new CustomException("해당 동아리의 탈퇴 요청이 아닙니다");
        }

        // 권한 체크
        if (!clubAuthorizationService.isClubAdmin(reviewer, request.getClub())) {
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
            ClubMember membership = clubMemberRepository.findByUserUserIdAndClubClubId(
                    request.getUser().getUserId(), request.getClub().getClubId())
                    .orElse(null);

            if (membership != null) {
                clubMemberRepository.delete(membership);
            }
        } else if ("REJECT".equalsIgnoreCase(action)) {
            request.setStatus(LeaveRequestStatus.REJECTED);
        } else {
            throw new CustomException("유효하지 않은 action입니다. APPROVE 또는 REJECT를 사용하세요.");
        }

        request.setReviewer(reviewer);
        request.setReviewNote(dto.getReviewNote());
        request.setReviewedAt(java.time.LocalDateTime.now());

        // 알림 트리거: 신청자에게 승인/거절 결과 알림
        User requester = request.getUser();
        String relatedUrl = String.format("/api/clubs/%d", request.getClub().getClubId());

        if (request.getStatus() == LeaveRequestStatus.APPROVED) {
            String content = String.format("'%s' 동아리 탈퇴가 승인되었습니다.", request.getClub().getClubName());
            notificationService.create(requester, NotificationType.LEAVE_APPROVED, content, relatedUrl);
        } else if (request.getStatus() == LeaveRequestStatus.REJECTED) {
            String content = String.format("'%s' 동아리 탈퇴가 거절되었습니다.", request.getClub().getClubName());
            notificationService.create(requester, NotificationType.LEAVE_REJECTED, content, relatedUrl);
        }

        return LeaveRequestDto.from(request);
    }
}
