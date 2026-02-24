package ycyh.uniclub.domain.schedule;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ScheduleResponseDto { // 일정 응답 DTO

    private Long scheduleId;
    private Long clubId;
    private String title;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String location;

    private Long createdByUserId;
    private String createdByName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
