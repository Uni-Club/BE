package ycyh.uniclub.domain.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import ycyh.uniclub.domain.user.*;
import ycyh.uniclub.global.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    
    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody UserSignupDto dto) {
        UserResponseDto userResponse = userService.signup(dto);
        return ResponseEntity.ok(userResponse);
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtTokenProvider.createToken(user.getEmail());

            return ResponseEntity.ok(LoginResponseDto.builder()
                    .token(token)
                    .user(UserResponseDto.from(user))
                    .build());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "이메일 또는 비밀번호가 올바르지 않습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "로그인 처리 중 오류가 발생했습니다."));
        }
    }
}


