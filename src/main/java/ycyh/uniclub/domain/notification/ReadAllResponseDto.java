package ycyh.uniclub.domain.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadAllResponseDto {
    private int updatedCount; // 이번에 읽음처리된 알림 개수
}
