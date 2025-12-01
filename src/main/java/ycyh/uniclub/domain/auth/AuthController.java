package ycyh.uniclub.domain.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import ycyh.uniclub.domain.user.*;
import ycyh.uniclub.global.security.JwtTokenProvider;
import ycyh.uniclub.global.security.TokenBlacklistService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.expiration}")
    private long tokenValidity;
    
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
                    .tokenType("Bearer")
                    .expiresIn(tokenValidity / 1000) // Convert milliseconds to seconds
                    .user(UserResponseDto.from(user))
                    .build());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "이메일 또는 비밀번호가 올바르지 않습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "로그인 처리 중 오류가 발생했습니다."));
        }
    }

    // 토큰 갱신 API
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@AuthenticationPrincipal User user) {
        try {
            String newToken = jwtTokenProvider.createToken(user.getEmail());
            return ResponseEntity.ok(Map.of(
                    "token", newToken,
                    "tokenType", "Bearer",
                    "expiresIn", tokenValidity / 1000,
                    "message", "토큰이 갱신되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "토큰 갱신 중 오류가 발생했습니다."));
        }
    }

    // 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                tokenBlacklistService.addToBlacklist(token);
                return ResponseEntity.ok(Map.of("message", "로그아웃되었습니다."));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "유효하지 않은 토큰입니다."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "로그아웃 처리 중 오류가 발생했습니다."));
        }
    }
}


