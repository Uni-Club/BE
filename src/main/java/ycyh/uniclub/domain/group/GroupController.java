package ycyh.uniclub.domain.group;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import ycyh.uniclub.domain.application.ApplicationService;
import ycyh.uniclub.domain.application.dto.ApplicationResponseDto;
import ycyh.uniclub.domain.recruitment.RecruitmentResponseDto;
import ycyh.uniclub.domain.recruitment.RecruitmentService;
import ycyh.uniclub.domain.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final RecruitmentService recruitmentService;
    private final ApplicationService applicationService;
    
    // 동아리 검색 API
    @GetMapping("/search")
    public ResponseEntity<List<GroupResponseDto>> searchGroups(
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
        
        GroupSearchDto searchDto = GroupSearchDto.builder()
                .keyword(keyword)
                .schoolId(schoolId)
                .category(category)
                .tags(tags)
                .build();
        
        return ResponseEntity.ok(groupService.searchGroups(searchDto));
    }

    // 동아리 상세 조회 API
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponseDto> getGroupDetail(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupDetail(groupId));
    }

    // 학교별 동아리 목록 조회 API
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<GroupResponseDto>> getGroupsBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(groupService.getGroupsBySchool(schoolId));
    }

    // 동아리 삭제 API
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User user) {
        groupService.deleteGroup(groupId, user);
        return ResponseEntity.noContent().build();
    }

    // 동아리 탈퇴 신청 API
    @PostMapping("/{groupId}/leave-request")
    public ResponseEntity<Void> requestLeave(
            @PathVariable Long groupId,
            @RequestBody LeaveRequestCreateDto dto,
            @AuthenticationPrincipal User user) {
        groupService.requestLeave(groupId, user, dto.getReason());
        return ResponseEntity.ok().build();
    }

    // 동아리 탈퇴 신청 목록 조회 API
    @GetMapping("/{groupId}/leave-requests")
    public ResponseEntity<List<LeaveRequestDto>> getLeaveRequests(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(groupService.getLeaveRequests(groupId, user));
    }

    // 내 탈퇴 신청 조회 API
    @GetMapping("/{groupId}/my-leave-request")
    public ResponseEntity<LeaveRequestDto> getMyLeaveRequest(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(groupService.getMyLeaveRequest(groupId, user));
    }

    // 탈퇴 신청 승인 API
    @PutMapping("/leave-requests/{requestId}/approve")
    public ResponseEntity<Void> approveLeaveRequest(
            @PathVariable Long requestId,
            @RequestBody LeaveRequestReviewDto dto,
            @AuthenticationPrincipal User user) {
        groupService.approveLeaveRequest(requestId, user, dto.getReviewNote());
        return ResponseEntity.ok().build();
    }

    // 탈퇴 신청 거절 API
    @PutMapping("/leave-requests/{requestId}/reject")
    public ResponseEntity<Void> rejectLeaveRequest(
            @PathVariable Long requestId,
            @RequestBody LeaveRequestReviewDto dto,
            @AuthenticationPrincipal User user) {
        groupService.rejectLeaveRequest(requestId, user, dto.getReviewNote());
        return ResponseEntity.ok().build();
    }

    // 동아리 멤버 삭제 API
    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @AuthenticationPrincipal User user) {
        groupService.removeMember(groupId, userId, user);
        return ResponseEntity.noContent().build();
    }

    // 동아리별 모집공고 목록 조회 API
    @GetMapping("/{groupId}/recruitments")
    public ResponseEntity<List<RecruitmentResponseDto>> getRecruitmentsByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(recruitmentService.getByGroup(groupId));
    }

    // 동아리별 지원서 목록 조회 API
    @GetMapping("/{groupId}/applications")
    public ResponseEntity<List<ApplicationResponseDto>> getApplicationsByGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getByGroup(groupId, user));
    }
}


