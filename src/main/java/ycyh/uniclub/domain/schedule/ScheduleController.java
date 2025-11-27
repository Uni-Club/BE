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
}
