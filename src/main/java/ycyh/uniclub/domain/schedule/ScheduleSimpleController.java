package ycyh.uniclub.domain.schedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.user.User;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleSimpleController {

    private final ScheduleService scheduleService;

    // 일정 생성 (groupId를 body에서 받음)
    @PostMapping
    public ResponseEntity<ScheduleResponseDto> createSchedule(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ScheduleCreateDto request
    ) {
        ScheduleResponseDto response = scheduleService.createScheduleSimple(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 일정 상세 조회 (scheduleId만으로)
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> getScheduleDetail(
            @PathVariable Long scheduleId
    ) {
        return ResponseEntity.ok(scheduleService.getScheduleById(scheduleId));
    }

    // 일정 수정 (scheduleId만으로)
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> updateSchedule(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ScheduleUpdateDto request
    ) {
        return ResponseEntity.ok(scheduleService.updateScheduleById(scheduleId, request, user));
    }

    // 일정 삭제 (scheduleId만으로)
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal User user
    ) {
        scheduleService.deleteScheduleById(scheduleId, user);
        return ResponseEntity.noContent().build();
    }
}
