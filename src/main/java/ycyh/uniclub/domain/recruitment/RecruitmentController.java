package ycyh.uniclub.domain.recruitment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.recruitment.dto.RecruitmentCreateDto;
import ycyh.uniclub.domain.recruitment.dto.RecruitmentUpdateDto;
import ycyh.uniclub.domain.user.User;

import java.util.List;

@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
public class RecruitmentController {
    private final RecruitmentService recruitmentService;

    @GetMapping("/{id}")
    public ResponseEntity<RecruitmentResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(recruitmentService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecruitmentResponseDto>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "PUBLISHED") RecruitmentStatus status
    ) {
        return ResponseEntity.ok(recruitmentService.search(keyword, schoolId, category, status));
    }
    
    @PostMapping
    public ResponseEntity<RecruitmentResponseDto> create(
            @RequestBody RecruitmentCreateDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(recruitmentService.create(dto, user));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<RecruitmentResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestParam RecruitmentStatus status,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(recruitmentService.updateStatus(id, status, user));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RecruitmentResponseDto> update(
            @PathVariable Long id,
            @RequestBody RecruitmentUpdateDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(recruitmentService.update(id, dto, user));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        recruitmentService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}


