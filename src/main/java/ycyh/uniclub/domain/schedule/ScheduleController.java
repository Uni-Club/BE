package ycyh.uniclub.domain.schedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.user.User;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 일정 생성 API
    @PostMapping
    public ResponseEntity<ScheduleResponseDto> createSchedule(
            @PathVariable("groupId") Long groupId,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ScheduleCreateDto request
    ) {
        // 경로에서 받은 groupId를 DTO에 세팅
        request.setGroupId(groupId);

        ScheduleResponseDto response = scheduleService.createSchedule(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 그룹별 일정 목록 조회 API
    @GetMapping
    public ResponseEntity<List<ScheduleResponseDto>> getSchedulesByGroup(
            @PathVariable("groupId") Long groupId
    ) {
        List<ScheduleResponseDto> schedules = scheduleService.getSchedulesByGroup(groupId);
        return ResponseEntity.ok(schedules);
    }

    // 특정 일정 상세 조회 API
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> getScheduleDetail(
            @PathVariable("groupId") Long groupId,
            @PathVariable("scheduleId") Long scheduleId
    ) {
        ScheduleResponseDto response = scheduleService.getScheduleDetail(groupId, scheduleId);
        return ResponseEntity.ok(response);
    }

    // 일정 수정 API
    @PatchMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> updateSchedule(
            @PathVariable("groupId") Long groupId,
            @PathVariable("scheduleId") Long scheduleId,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ScheduleUpdateDto request
    ) {
        ScheduleResponseDto response = scheduleService.updateSchedule(groupId, scheduleId, request, user);
        return ResponseEntity.ok(response);
    }

    // 일정 삭제 API
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable("groupId") Long groupId,
            @PathVariable("scheduleId") Long scheduleId,
            @AuthenticationPrincipal User user
    ) {
        scheduleService.deleteSchedule(groupId, scheduleId, user);
        return ResponseEntity.noContent().build();
    }
}
