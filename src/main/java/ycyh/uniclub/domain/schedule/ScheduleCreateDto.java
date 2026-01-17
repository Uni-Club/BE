package ycyh.uniclub.domain.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ScheduleCreateDto { // 일정 생성 요청 DTO

    private Long groupId;

    @NotBlank
    private String title;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endAt;

    private String location;

    // FE 호환: date 필드 (startAt/endAt 대신 사용 가능)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    // FE 호환: date가 있으면 startAt으로 변환
    public LocalDateTime getStartAt() {
        if (startAt != null) return startAt;
        if (date != null) return date.atStartOfDay();
        return null;
    }

    // FE 호환: date가 있으면 endAt으로 변환
    public LocalDateTime getEndAt() {
        if (endAt != null) return endAt;
        if (date != null) return date.atTime(LocalTime.MAX);
        return null;
    }
}
