package ycyh.uniclub.domain.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleCreateDto { // 일정 생성 요청 DTO

    private Long groupId;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    @Future(message = "일정 시간은 현재 시각 이후여야 합니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;


}
