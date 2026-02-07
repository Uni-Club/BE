package ycyh.uniclub.domain.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // 알림 생성 (with NotificationType)
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

    // 알림 목록 조회 (페이지네이션)
    public Page<NotificationResponseDto> getMyNotifications(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return notificationRepository
                .findByUser_UserIdOrderByCreatedAtDesc(user.getUserId(), pageable)
                .map(NotificationResponseDto::from);
    }

    // 미읽음만 조회 (페이지네이션)
    public Page<NotificationResponseDto> getMyUnreadNotification(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return notificationRepository
                .findByUser_UserIdAndReadFalseOrderByCreatedAtDesc(user.getUserId(), pageable)
                .map(NotificationResponseDto::from);
    }

    // 미읽음 개수
    public UnreadCountResponseDto getMyUnreadCount(User user) {
        long count = notificationRepository.countByUser_UserIdAndReadFalse(user.getUserId());

        return new UnreadCountResponseDto(count);
    }

    // 단건 읽음 처리
    @Transactional
    public NotificationResponseDto markAsRead(User user, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException("알림을 찾을 수 없습니다: " + notificationId));

        // 내 알림만 처리 가능
        if (!notification.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException("본인의 알림만 읽음 처리할 수 있습니다");
        }

        if (!notification.isRead()) {
            notification.markAsRead();
        }

        return NotificationResponseDto.from(notification);
    }

    // 전체 읽음 처리 (벌크 업데이트)
    @Transactional
    public ReadAllResponseDto markAllAsRead(User user) {
        int updated = notificationRepository.markAllAsRead(user.getUserId());

        return new ReadAllResponseDto(updated);
    }

    // 오래된 읽은 알림 삭제 (30일 이상)
    @Transactional
    public void deleteOldNotifications(User user) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Notification> oldRead = notificationRepository.findByUserAndReadTrueAndCreatedAtBefore(user, thirtyDaysAgo);
        notificationRepository.deleteAll(oldRead);
    }
}
