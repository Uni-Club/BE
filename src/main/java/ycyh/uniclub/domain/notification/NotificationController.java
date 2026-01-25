package ycyh.uniclub.domain.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.user.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    // 알림 목록 조회 (최신순)
    @GetMapping
    public ResponseEntity<Page<NotificationResponseDto>> getMyNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(notificationService.getMyNotifications(user, page, size));
    }

    // 미읽음 목록 조회
    @GetMapping("/unread")
    public ResponseEntity<Page<NotificationResponseDto>> getMyUnreadNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(notificationService.getMyUnreadNotification(user, page, size));
    }

    // 미읽음 개수 조회
    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponseDto> getUnreadCount(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(notificationService.getMyUnreadCount(user));
    }

    // 단건 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponseDto> markAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable Long notificationId
    ) {
        return ResponseEntity.ok(notificationService.markAsRead(user, notificationId));
    }

    // 전체 읽음 처리
    @PatchMapping("/read-all")
    public ResponseEntity<ReadAllResponseDto> markAllAsRead(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(notificationService.markAllAsRead(user));
    }
}
