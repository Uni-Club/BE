package ycyh.uniclub.domain.notification;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDto {
    private Long notificationId;
    private String content;
    private String relatedUrl;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponseDto from(Notification n) {
        return NotificationResponseDto.builder()
                .notificationId(n.getNotificationId())
                .content(n.getContent())
                .relatedUrl(n.getRelatedUrl())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
