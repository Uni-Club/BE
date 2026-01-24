package ycyh.uniclub.domain.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.user.User;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 알림 생성
    @Transactional
    public void create(User receiver, NotificationType type, String content, String relatedUrl) {
        Notification notification = Notification.builder()
                .user(receiver)
                .type(type)
                .content(content)
                .relatedUrl(relatedUrl)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    // 알림 목록 조회
    @Transactional
    public Page<NotificationResponseDto> getMyNotifications(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return notificationRepository
                .findByUser_UserIdOrderByCreatedAtDesc(user.getUserId(), pageable)
                .map(NotificationResponseDto::from);
    }

    // 미읽음만 조회
    @Transactional
    public Page<NotificationResponseDto> getMyUnreadNotification(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return notificationRepository
                .findByUser_UserIdAndReadFalseOrderByCreatedAtDesc(user.getUserId(), pageable)
                .map(NotificationResponseDto::from);
    }

    // 미읽음 개수
    @Transactional
    public UnreadCountResponseDto getMyUnreadCount(User user) {
        long count = notificationRepository.countByUser_UserIdAndReadFalse(user.getUserId());

        return new UnreadCountResponseDto(count);
    }

    // 단건 읽음
    @Transactional
    public NotificationResponseDto markAsRead(User user, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));

        // 내 알림만 처리 가능
        if (!notification.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("Forbidden: not your notification");
        }

        if (!notification.isRead()) {
            notification.markAsRead();
        }

        return NotificationResponseDto.from(notification);
    }

    // 전체 읽음
    @Transactional
    public ReadAllResponseDto markAllAsRead(User user) {
        int updated = notificationRepository.markAllAsRead(user.getUserId());

        return new ReadAllResponseDto(updated);
    }
}
