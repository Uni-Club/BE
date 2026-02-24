package ycyh.uniclub.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.application.ApplicationResponseDto;
import ycyh.uniclub.domain.application.ApplicationService;
import ycyh.uniclub.domain.club.ClubMember;
import ycyh.uniclub.domain.club.ClubMemberRepository;
import ycyh.uniclub.domain.me.MyClubDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ClubMemberRepository clubMemberRepository;
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
        // FE 호환: currentPassword 또는 oldPassword 둘 다 지원
        String oldPassword = passwords.get("currentPassword");
        if (oldPassword == null) {
            oldPassword = passwords.get("oldPassword");
        }
        userService.changePassword(
                user.getUserId(),
                oldPassword,
                passwords.get("newPassword")
        );
        return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다"));
    }

    // 내 동아리 목록 조회 API
    @GetMapping("/me/clubs")
    public ResponseEntity<List<MyClubDto>> getMyClubs(@AuthenticationPrincipal User user) {
        List<ClubMember> memberships = clubMemberRepository.findByUserUserId(user.getUserId());
        List<MyClubDto> result = memberships.stream()
                .map(MyClubDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // 내 지원 내역 조회 API
    @GetMapping("/me/applications")
    public ResponseEntity<List<ApplicationResponseDto>> getMyApplications(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getMyApplications(user));
    }
}


