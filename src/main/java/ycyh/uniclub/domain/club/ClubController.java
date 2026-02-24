package ycyh.uniclub.domain.club;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import ycyh.uniclub.domain.application.ApplicationService;
import ycyh.uniclub.domain.application.ApplicationResponseDto;
import ycyh.uniclub.domain.recruitment.RecruitmentResponseDto;
import ycyh.uniclub.domain.recruitment.RecruitmentService;
import ycyh.uniclub.domain.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {
    private final ClubService clubService;
    private final RecruitmentService recruitmentService;
    private final ApplicationService applicationService;

    // 동아리 검색 API
    @GetMapping
    public ResponseEntity<List<ClubResponseDto>> searchClubs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tags,
            Authentication authentication) {
        // 기본 정책: schoolId 미지정 시 로그인 사용자의 소속 학교 자동 적용
        if (schoolId == null && authentication != null && authentication.getPrincipal() instanceof User user) {
            if (user.getSchool() != null) {
                schoolId = user.getSchool().getSchoolId();
            }
        }

        ClubSearchDto searchDto = ClubSearchDto.builder()
                .keyword(keyword)
                .schoolId(schoolId)
                .category(category)
                .tags(tags)
                .build();

        return ResponseEntity.ok(clubService.searchClubs(searchDto));
    }

    // 동아리 생성 API
    @PostMapping
    public ResponseEntity<ClubResponseDto> createClub(
            @RequestBody ClubCreateDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clubService.createClub(dto, user));
    }

    // 동아리 상세 조회 API
    @GetMapping("/{clubId}")
    public ResponseEntity<ClubResponseDto> getClubDetail(@PathVariable Long clubId) {
        return ResponseEntity.ok(clubService.getClubDetail(clubId));
    }

    // 동아리 수정 API
    @PatchMapping("/{clubId}")
    public ResponseEntity<ClubResponseDto> updateClub(
            @PathVariable Long clubId,
            @RequestBody ClubUpdateDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clubService.updateClub(clubId, dto, user));
    }

    // 학교별 동아리 목록 조회 API
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<ClubResponseDto>> getClubsBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(clubService.getClubsBySchool(schoolId));
    }

    // 동아리 삭제 API
    @DeleteMapping("/{clubId}")
    public ResponseEntity<Void> deleteClub(
            @PathVariable Long clubId,
            @AuthenticationPrincipal User user) {
        clubService.deleteClub(clubId, user);
        return ResponseEntity.noContent().build();
    }

    // 멤버 목록 조회 API
    @GetMapping("/{clubId}/members")
    public ResponseEntity<List<ClubMemberDto>> getMembers(@PathVariable Long clubId) {
        return ResponseEntity.ok(clubService.getMembers(clubId));
    }

    // 멤버 추가 API
    @PostMapping("/{clubId}/members")
    public ResponseEntity<ClubMemberDto> addMember(
            @PathVariable Long clubId,
            @RequestBody ClubMemberAddDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clubService.addMember(clubId, dto, user));
    }

    // 동아리 탈퇴 신청 API
    @PostMapping("/{clubId}/leave")
    public ResponseEntity<Void> requestLeave(
            @PathVariable Long clubId,
            @RequestBody LeaveRequestCreateDto dto,
            @AuthenticationPrincipal User user) {
        clubService.requestLeave(clubId, user, dto.getReason());
        return ResponseEntity.ok().build();
    }

    // 동아리 탈퇴 신청 목록 조회 API
    @GetMapping("/{clubId}/leave-requests")
    public ResponseEntity<List<LeaveRequestDto>> getLeaveRequests(
            @PathVariable Long clubId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clubService.getLeaveRequests(clubId, user));
    }

    // 내 탈퇴 신청 조회 API
    @GetMapping("/{clubId}/my-leave-request")
    public ResponseEntity<LeaveRequestDto> getMyLeaveRequest(
            @PathVariable Long clubId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clubService.getMyLeaveRequest(clubId, user));
    }

    // 탈퇴 신청 처리 API (승인/거절)
    @PatchMapping("/{clubId}/leave-requests/{requestId}")
    public ResponseEntity<LeaveRequestDto> processLeaveRequest(
            @PathVariable Long clubId,
            @PathVariable Long requestId,
            @RequestBody LeaveRequestReviewDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clubService.processLeaveRequest(clubId, requestId, dto, user));
    }

    // 동아리 멤버 삭제 API
    @DeleteMapping("/{clubId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long clubId,
            @PathVariable Long userId,
            @AuthenticationPrincipal User user) {
        clubService.removeMember(clubId, userId, user);
        return ResponseEntity.noContent().build();
    }

    // 동아리별 모집공고 목록 조회 API
    @GetMapping("/{clubId}/recruitments")
    public ResponseEntity<List<RecruitmentResponseDto>> getRecruitmentsByClub(@PathVariable Long clubId) {
        return ResponseEntity.ok(recruitmentService.getByClub(clubId));
    }

    // 동아리별 지원서 목록 조회 API
    @GetMapping("/{clubId}/applications")
    public ResponseEntity<List<ApplicationResponseDto>> getApplicationsByClub(
            @PathVariable Long clubId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getByClub(clubId, user));
    }
}


