package com.example.flexapp.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static final int MAX_IP_FAILURES = 10;
    private static final int MAX_EMAIL_FAILURES = 5;
    private static final long WINDOW_SECONDS = 15 * 60; // 15 minuter
    private static final long ENTRY_TTL_SECONDS = WINDOW_SECONDS * 2;

    private static class Attempt {
        int count;
        Instant windowStart;
        Instant lastSeen;

        Attempt(Instant now) {
            this.count = 0;
            this.windowStart = now;
            this.lastSeen = now;
        }

        void resetWindow(Instant now) {
            this.count = 0;
            this.windowStart = now;
            this.lastSeen = now;
        }

        boolean isExpired(Instant now) {
            return now.isAfter(lastSeen.plusSeconds(ENTRY_TTL_SECONDS));
        }
    }

    private final Map<String, Attempt> ipAttempts = new ConcurrentHashMap<>();
    private final Map<String, Attempt> emailAttempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String clientIp, String email) {
        Instant now = Instant.now();

        String normalizedIp = normalizeIp(clientIp);
        String normalizedEmail = normalizeEmail(email);

        return isBlocked(ipAttempts, normalizedIp, MAX_IP_FAILURES, now)
                || isBlocked(emailAttempts, normalizedEmail, MAX_EMAIL_FAILURES, now);
    }

    public void recordFailure(String clientIp, String email) {
        Instant now = Instant.now();

        increment(ipAttempts, normalizeIp(clientIp), now);
        increment(emailAttempts, normalizeEmail(email), now);
    }

    public void recordSuccess(String email) {
        String normalizedEmail = normalizeEmail(email);
        emailAttempts.remove(normalizedEmail);
    }

    private boolean isBlocked(Map<String, Attempt> attemptsMap,
                              String key,
                              int maxFailures,
                              Instant now) {

        Attempt attempt = attemptsMap.compute(key, (k, existing) -> {
            if (existing == null || existing.isExpired(now)) {
                return new Attempt(now);
            }
            return existing;
        });

        synchronized (attempt) {
            if (now.isAfter(attempt.windowStart.plusSeconds(WINDOW_SECONDS))) {
                attempt.resetWindow(now);
                return false;
            }

            attempt.lastSeen = now;
            return attempt.count >= maxFailures;
        }
    }

    private void increment(Map<String, Attempt> attemptsMap, String key, Instant now) {
        Attempt attempt = attemptsMap.compute(key, (k, existing) -> {
            if (existing == null || existing.isExpired(now)) {
                return new Attempt(now);
            }
            return existing;
        });

        synchronized (attempt) {
            if (now.isAfter(attempt.windowStart.plusSeconds(WINDOW_SECONDS))) {
                attempt.resetWindow(now);
            }

            attempt.count++;
            attempt.lastSeen = now;
        }
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "unknown-email";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeIp(String clientIp) {
        if (clientIp == null || clientIp.isBlank()) {
            return "unknown-ip";
        }
        return clientIp.trim();
    }

    @Scheduled(fixedDelay = 60_000)
    public void cleanupExpiredAttempts() {
        Instant now = Instant.now();
        ipAttempts.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
        emailAttempts.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
    }
}