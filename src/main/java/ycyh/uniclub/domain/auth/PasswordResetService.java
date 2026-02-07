package ycyh.uniclub.domain.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.user.User;
import ycyh.uniclub.domain.user.UserRepository;
import ycyh.uniclub.global.exception.CustomException;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // code -> ResetEntry (email, code, expiryTime)
    private final ConcurrentHashMap<String, ResetEntry> resetCodes = new ConcurrentHashMap<>();

    private static final long EXPIRY_MILLIS = 5 * 60 * 1000; // 5 minutes

    public void requestReset(String email) {
        // 사용자 존재 확인
        userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("해당 이메일로 등록된 사용자가 없습니다"));

        // 6자리 코드 생성
        String code = String.format("%06d", new Random().nextInt(1000000));
        long expiryTime = Instant.now().toEpochMilli() + EXPIRY_MILLIS;

        resetCodes.put(email, new ResetEntry(code, expiryTime));

        // 실제 이메일 발송 대신 로그 출력
        log.info("비밀번호 재설정 코드 [{}]: {}", email, code);
    }

    @Transactional
    public void verifyAndReset(String email, String code, String newPassword) {
        ResetEntry entry = resetCodes.get(email);

        if (entry == null) {
            throw new CustomException("비밀번호 재설정 요청이 없습니다");
        }

        if (Instant.now().toEpochMilli() > entry.expiryTime()) {
            resetCodes.remove(email);
            throw new CustomException("인증 코드가 만료되었습니다");
        }

        if (!entry.code().equals(code)) {
            throw new CustomException("인증 코드가 일치하지 않습니다");
        }

        // 비밀번호 변경
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 사용된 코드 제거
        resetCodes.remove(email);
    }

    private record ResetEntry(String code, long expiryTime) {}
}
