package ycyh.uniclub.domain.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 내 알림 목록 (최신순)
    Page<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 내 미읽음 알림 목록 (최신순)
    Page<Notification> findByUser_UserIdAndReadFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 미읽음 개수 (배지)
    long countByUser_UserIdAndReadFalse(Long userId);

    // 전체 읽음 처리 (벌크 업데이트)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                update Notification n
                set n.read = true
                where n.user.userId = :userId
                    and n.read = false
            """)
    int markAllAsRead(@Param("userId") Long userId);
}
