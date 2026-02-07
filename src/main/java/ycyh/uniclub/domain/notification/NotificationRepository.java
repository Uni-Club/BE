package ycyh.uniclub.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ycyh.uniclub.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    long countByUserAndIsReadFalse(User user);
    List<Notification> findByUserAndIsReadTrueAndCreatedAtBefore(User user, LocalDateTime dateTime);
}
