package ycyh.uniclub.domain.schedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 일정 생성 API
    @PostMapping
    public ResponseEntity<ScheduleResponseDto> createSchedule(
            @RequestBody @Valid ScheduleCreateDto request
    ) {
        ScheduleResponseDto response = scheduleService.createSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 그룹별 일정 목록 조회 API
    @GetMapping
    public ResponseEntity<List<ScheduleResponseDto>> getSchedulesByGroup(
            @RequestParam("groupId") Long groupId
    ) {
        List<ScheduleResponseDto> schedules = scheduleService.getSchedulesByGroup(groupId);
        return ResponseEntity.ok(schedules);
    }
}
