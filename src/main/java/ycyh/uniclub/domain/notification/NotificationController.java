package ycyh.uniclub.domain.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.user.User;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // 내 알림 목록 조회
    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getMyNotifications(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.getMyNotifications(user));
    }

    // 읽지 않은 알림 개수 조회
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal User user) {
        long count = notificationService.getUnreadCount(user);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    // 알림 읽음 처리
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        notificationService.markAsRead(id, user);
        return ResponseEntity.ok().build();
    }

    // 전체 알림 읽음 처리
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok().build();
    }
}
