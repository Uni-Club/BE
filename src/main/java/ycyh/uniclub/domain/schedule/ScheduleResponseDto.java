package ycyh.uniclub.domain.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
public class ScheduleResponseDto { // 일정 응답 DTO

    private Long scheduleId;
    private Long groupId;
    private String title;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String location;

    private Long createdByUserId;
    private String createdByName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // FE 호환: date 필드 추가 (startAt의 날짜 부분)
    public LocalDate getDate() {
        return startAt != null ? startAt.toLocalDate() : null;
    }
}
