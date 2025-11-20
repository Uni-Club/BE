package ycyh.uniclub.domain.me;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ycyh.uniclub.domain.group.GroupMember;
import ycyh.uniclub.domain.group.GroupMemberRepository;
import ycyh.uniclub.domain.user.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {
    private final GroupMemberRepository groupMemberRepository;

    // 내 동아리 목록 조회 API
    @GetMapping("/groups")
    public ResponseEntity<List<MyGroupDto>> myGroups(@AuthenticationPrincipal User user) {
        List<GroupMember> memberships = groupMemberRepository.findByUserUserId(user.getUserId());
        List<MyGroupDto> result = memberships.stream().map(MyGroupDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}


