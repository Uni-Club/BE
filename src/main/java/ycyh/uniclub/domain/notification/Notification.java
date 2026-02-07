package ycyh.uniclub.domain.notification;

import jakarta.persistence.*;
import lombok.*;
import ycyh.uniclub.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "notification")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(name = "related_url", length = 255)
    private String relatedUrl;

    @Column(name = "is_read")
    @Builder.Default
    private boolean isRead = false;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public void markAsRead() {
        this.isRead = true;
    }
}
