package ycyh.uniclub.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.application.ApplicationResponseDto;
import ycyh.uniclub.domain.application.ApplicationService;
import ycyh.uniclub.domain.group.GroupMember;
import ycyh.uniclub.domain.group.GroupMemberRepository;
import ycyh.uniclub.domain.me.MyGroupDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final GroupMemberRepository groupMemberRepository;
    private final ApplicationService applicationService;

    // 내 정보 조회 API
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserInfo(user.getUserId()));
    }

    // 내 정보 수정 API
    @PatchMapping("/me")
    public ResponseEntity<UserResponseDto> updateMyInfo(
            @AuthenticationPrincipal User user,
            @RequestBody UserUpdateDto dto) {
        return ResponseEntity.ok(userService.updateUser(user.getUserId(), dto));
    }

    // 비밀번호 변경 API
    @PatchMapping("/me/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> passwords) {
        userService.changePassword(
                user.getUserId(),
                passwords.get("oldPassword"),
                passwords.get("newPassword")
        );
        return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다"));
    }

    // 내 동아리 목록 조회 API
    @GetMapping("/me/groups")
    public ResponseEntity<List<MyGroupDto>> getMyGroups(@AuthenticationPrincipal User user) {
        List<GroupMember> memberships = groupMemberRepository.findByUserUserId(user.getUserId());
        List<MyGroupDto> result = memberships.stream()
                .map(MyGroupDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // 내 지원 내역 조회 API
    @GetMapping("/me/applications")
    public ResponseEntity<List<ApplicationResponseDto>> getMyApplications(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getMyApplications(user));
    }
}


