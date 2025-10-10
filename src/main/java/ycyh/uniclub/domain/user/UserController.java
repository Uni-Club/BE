package ycyh.uniclub.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserInfo(user.getUserId()));
    }
    
    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateMyInfo(
            @AuthenticationPrincipal User user,
            @RequestBody UserUpdateDto dto) {
        return ResponseEntity.ok(userService.updateUser(user.getUserId(), dto));
    }
    
    @PutMapping("/me/password")
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
}


