package ycyh.uniclub.domain.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {
    List<ClubMember> findByClubClubId(Long clubId);
    List<ClubMember> findByUserUserId(Long userId);
    Optional<ClubMember> findByUserUserIdAndClubClubId(Long userId, Long clubId);
    boolean existsByUserUserIdAndClubClubId(Long userId, Long clubId);
}


