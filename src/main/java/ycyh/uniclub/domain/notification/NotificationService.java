package ycyh.uniclub.domain.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.user.User;
import ycyh.uniclub.global.exception.CustomException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<NotificationResponseDto> getMyNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Transactional
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException("알림을 찾을 수 없습니다"));

        if (!notification.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException("본인의 알림만 읽음 처리할 수 있습니다");
        }

        notification.markAsRead();
    }

    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> unread = notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
        for (Notification notification : unread) {
            notification.markAsRead();
        }
    }

    @Transactional
    public void createNotification(User user, String content, String relatedUrl) {
        Notification notification = Notification.builder()
                .user(user)
                .content(content)
                .relatedUrl(relatedUrl)
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void deleteOldNotifications(User user) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Notification> oldRead = notificationRepository.findByUserAndIsReadTrueAndCreatedAtBefore(user, thirtyDaysAgo);
        notificationRepository.deleteAll(oldRead);
    }
}
