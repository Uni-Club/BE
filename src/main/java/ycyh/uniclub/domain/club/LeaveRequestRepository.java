package ycyh.uniclub.domain.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByClubClubIdAndStatus(Long clubId, LeaveRequestStatus status);
    List<LeaveRequest> findByClubClubId(Long clubId);
    Optional<LeaveRequest> findByClubClubIdAndUserUserIdAndStatus(Long clubId, Long userId, LeaveRequestStatus status);
    boolean existsByClubClubIdAndUserUserIdAndStatus(Long clubId, Long userId, LeaveRequestStatus status);
}
