package ycyh.uniclub.domain.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.application.dto.ApplicationSubmitDto;
import ycyh.uniclub.domain.application.dto.ApplicationResponseDto;
import ycyh.uniclub.domain.application.dto.ApplicationReviewDto;
import ycyh.uniclub.domain.user.User;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;
    
    @PostMapping
    public ResponseEntity<ApplicationResponseDto> submit(
            @RequestBody ApplicationSubmitDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.submit(dto, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponseDto> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getById(id, user));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationResponseDto> review(
            @PathVariable Long id,
            @RequestBody ApplicationReviewDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.review(id, dto, user));
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<ApplicationResponseDto>> getMyApplications(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getMyApplications(user));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        applicationService.cancel(id, user);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/recruitment/{recruitmentId}/status")
    public ResponseEntity<ApplicationResponseDto> getMyApplicationStatus(
            @PathVariable Long recruitmentId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getMyApplicationStatus(recruitmentId, user));
    }
}
