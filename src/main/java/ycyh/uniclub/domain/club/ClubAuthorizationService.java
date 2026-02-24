package ycyh.uniclub.domain.club;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.user.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubAuthorizationService {
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;

    public boolean isClubAdmin(User user, Club club) {
        // 그룹 리더인지 확인
        if (club.getLeaderId() != null && club.getLeaderId().equals(user.getUserId())) {
            return true;
        }

        // 그룹 멤버 중 관리자 권한이 있는지 확인
        return clubMemberRepository.findByUserUserIdAndClubClubId(user.getUserId(), club.getClubId())
                .map(member -> {
                    String role = member.getRole();
                    return "회장".equals(role) || "부회장".equals(role) || "관리자".equals(role);
                })
                .orElse(false);
    }

    public boolean isClubAdmin(User user, Long clubId) {
        Club club = clubRepository.findById(clubId).orElse(null);
        if (club == null) {
            return false;
        }
        return isClubAdmin(user, club);
    }

    public boolean isClubMember(User user, Long clubId) {
        return clubMemberRepository.existsByUserUserIdAndClubClubId(user.getUserId(), clubId);
    }

    public Optional<String> getRole(User user, Long clubId) {
        return clubMemberRepository.findByUserUserIdAndClubClubId(user.getUserId(), clubId)
                .map(ClubMember::getRole);
    }
}
