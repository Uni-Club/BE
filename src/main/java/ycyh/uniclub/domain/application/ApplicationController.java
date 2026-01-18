package ycyh.uniclub.domain.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.user.User;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;
    
    // 지원서 제출 API
    @PostMapping
    public ResponseEntity<ApplicationResponseDto> submit(
            @RequestBody ApplicationSubmitDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.submit(dto, user));
    }

    // 지원서 상세 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponseDto> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getById(id, user));
    }

    // 지원서 심사 API
    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationResponseDto> review(
            @PathVariable Long id,
            @RequestBody ApplicationReviewDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.review(id, dto, user));
    }

    // 지원서 심사 API (FE 호환: PATCH /applications/{id})
    @PatchMapping("/{id}")
    public ResponseEntity<ApplicationResponseDto> reviewPatch(
            @PathVariable Long id,
            @RequestBody ApplicationReviewDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.review(id, dto, user));
    }

    // 내 지원 내역 조회 API
    @GetMapping("/my")
    public ResponseEntity<List<ApplicationResponseDto>> getMyApplications(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getMyApplications(user));
    }

    // 지원서 취소 API
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        applicationService.cancel(id, user);
        return ResponseEntity.ok().build();
    }

    // 모집공고별 내 지원 상태 조회 API
    @GetMapping("/recruitment/{recruitmentId}/status")
    public ResponseEntity<ApplicationResponseDto> getMyApplicationStatus(
            @PathVariable Long recruitmentId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getMyApplicationStatus(recruitmentId, user));
    }
}
