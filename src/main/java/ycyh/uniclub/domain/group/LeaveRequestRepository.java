package ycyh.uniclub.domain.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByGroupGroupIdAndStatus(Long groupId, LeaveRequestStatus status);
    List<LeaveRequest> findByGroupGroupId(Long groupId);
    Optional<LeaveRequest> findByGroupGroupIdAndUserUserIdAndStatus(Long groupId, Long userId, LeaveRequestStatus status);
    boolean existsByGroupGroupIdAndUserUserIdAndStatus(Long groupId, Long userId, LeaveRequestStatus status);
}
