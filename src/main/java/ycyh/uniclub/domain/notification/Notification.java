package ycyh.uniclub.domain.notification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ycyh.uniclub.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notification",
        indexes = {
                @Index(name = "idx_notification_user_created_at", columnList = "user_id, created_at"),
                @Index(name = "idx_notification_user_is_read_created_at", columnList = "user_id, is_read, created_at")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "notification_id")
        private Long notificationId;

        // 수신자
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        // 알림 타입
        @Enumerated(EnumType.STRING)
        @Column(name = "type", nullable = false, length = 50)
        private NotificationType type;

        // 알림 내용
        @Column(name = "content", nullable = false, length = 200)
        private String content;

        // 관련 링크
        @Column(name = "related_url", nullable = false, length = 500)
        private String relatedUrl;

        @Column(name = "is_read", nullable = false)
        @Builder.Default
        private boolean read = false;

        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @PrePersist
        private void prePersist() {
                this.createdAt = LocalDateTime.now();
        }

        public void markAsRead() {
                this.read = true;
        }
}
