package ycyh.uniclub.domain.group;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import ycyh.uniclub.domain.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    
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
    
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponseDto> getGroupDetail(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupDetail(groupId));
    }
    
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<GroupResponseDto>> getGroupsBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(groupService.getGroupsBySchool(schoolId));
    }
    
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User user) {
        groupService.deleteGroup(groupId, user);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{groupId}/leave-request")
    public ResponseEntity<Void> requestLeave(
            @PathVariable Long groupId,
            @RequestBody LeaveRequestCreateDto dto,
            @AuthenticationPrincipal User user) {
        groupService.requestLeave(groupId, user, dto.getReason());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{groupId}/leave-requests")
    public ResponseEntity<List<LeaveRequestDto>> getLeaveRequests(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(groupService.getLeaveRequests(groupId, user));
    }
    
    @GetMapping("/{groupId}/my-leave-request")
    public ResponseEntity<LeaveRequestDto> getMyLeaveRequest(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(groupService.getMyLeaveRequest(groupId, user));
    }
    
    @PutMapping("/leave-requests/{requestId}/approve")
    public ResponseEntity<Void> approveLeaveRequest(
            @PathVariable Long requestId,
            @RequestBody LeaveRequestReviewDto dto,
            @AuthenticationPrincipal User user) {
        groupService.approveLeaveRequest(requestId, user, dto.getReviewNote());
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/leave-requests/{requestId}/reject")
    public ResponseEntity<Void> rejectLeaveRequest(
            @PathVariable Long requestId,
            @RequestBody LeaveRequestReviewDto dto,
            @AuthenticationPrincipal User user) {
        groupService.rejectLeaveRequest(requestId, user, dto.getReviewNote());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @AuthenticationPrincipal User user) {
        groupService.removeMember(groupId, userId, user);
        return ResponseEntity.noContent().build();
    }
}


