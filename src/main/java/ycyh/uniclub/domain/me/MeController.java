package ycyh.uniclub.domain.me;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ycyh.uniclub.domain.club.ClubMember;
import ycyh.uniclub.domain.club.ClubMemberRepository;
import ycyh.uniclub.domain.user.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {
    private final ClubMemberRepository clubMemberRepository;

    // 내 동아리 목록 조회 API
    @GetMapping("/clubs")
    public ResponseEntity<List<MyClubDto>> myClubs(@AuthenticationPrincipal User user) {
        List<ClubMember> memberships = clubMemberRepository.findByUserUserId(user.getUserId());
        List<MyClubDto> result = memberships.stream().map(MyClubDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}


