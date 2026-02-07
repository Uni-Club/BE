package ycyh.uniclub.global.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TokenBlacklistService {

    // token -> expiry time in epoch milliseconds
    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();

    public void addToBlacklist(String token) {
        // Default expiry: 24 hours from now
        blacklist.put(token, Instant.now().toEpochMilli() + 24 * 60 * 60 * 1000);
    }

    public void addToBlacklist(String token, long expiryTimeMillis) {
        blacklist.put(token, expiryTimeMillis);
    }

    public boolean isBlacklisted(String token) {
        Long expiry = blacklist.get(token);
        if (expiry == null) {
            return false;
        }
        // If the token has expired from the blacklist, remove it and return false
        if (Instant.now().toEpochMilli() > expiry) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }

    public void removeFromBlacklist(String token) {
        blacklist.remove(token);
    }

    // 매 시간마다 만료된 토큰을 정리
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void cleanupExpiredTokens() {
        long now = Instant.now().toEpochMilli();
        int removedCount = 0;

        Iterator<Map.Entry<String, Long>> iterator = blacklist.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (now > entry.getValue()) {
                iterator.remove();
                removedCount++;
            }
        }

        if (removedCount > 0) {
            log.info("블랙리스트에서 만료된 토큰 {}개를 정리했습니다. 남은 토큰: {}개", removedCount, blacklist.size());
        }
    }
}
