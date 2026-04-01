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
        int failures;
        int inFlight;
        Instant windowStart;
        Instant lastSeen;

        Attempt(Instant now) {
            this.failures = 0;
            this.inFlight = 0;
            this.windowStart = now;
            this.lastSeen = now;
        }

        void resetWindow(Instant now) {
            this.failures = 0;
            this.inFlight = 0;
            this.windowStart = now;
            this.lastSeen = now;
        }

        boolean isExpired(Instant now) {
            return inFlight == 0 && now.isAfter(lastSeen.plusSeconds(ENTRY_TTL_SECONDS));
        }
    }

    private final Map<String, Attempt> ipAttempts = new ConcurrentHashMap<>();
    private final Map<String, Attempt> emailAttempts = new ConcurrentHashMap<>();

    public boolean tryAcquire(String clientIp, String email) {
        Instant now = Instant.now();

        String normalizedIp = normalizeIp(clientIp);
        String normalizedEmail = normalizeEmail(email);

        if (!tryAcquire(ipAttempts, normalizedIp, MAX_IP_FAILURES, now)) {
            return false;
        }

        if (!tryAcquire(emailAttempts, normalizedEmail, MAX_EMAIL_FAILURES, now)) {
            release(ipAttempts, normalizedIp, now);
            return false;
        }

        return true;
    }

    public void recordFailure(String clientIp, String email) {
        Instant now = Instant.now();

        recordFailure(ipAttempts, normalizeIp(clientIp), now);
        recordFailure(emailAttempts, normalizeEmail(email), now);
    }

    public void recordSuccess(String clientIp, String email) {
        clear(ipAttempts, normalizeIp(clientIp));
        clear(emailAttempts, normalizeEmail(email));
    }

    private boolean tryAcquire(Map<String, Attempt> attemptsMap,
                               String key,
                               int maxFailures,
                               Instant now) {

        final boolean[] allowed = {false};

        attemptsMap.compute(key, (k, existing) -> {
            Attempt attempt = getCurrentAttempt(existing, now);
            attempt.lastSeen = now;

            if (attempt.failures + attempt.inFlight >= maxFailures) {
                allowed[0] = false;
                return attempt;
            }

            attempt.inFlight++;
            allowed[0] = true;
            return attempt;
        });

        return allowed[0];
    }

    private void recordFailure(Map<String, Attempt> attemptsMap, String key, Instant now) {
        attemptsMap.compute(key, (k, existing) -> {
            Attempt attempt = getCurrentAttempt(existing, now);
            attempt.lastSeen = now;

            if (attempt.inFlight > 0) {
                attempt.inFlight--;
            }

            attempt.failures++;
            return attempt;
        });
    }

    private void release(Map<String, Attempt> attemptsMap, String key, Instant now) {
        attemptsMap.computeIfPresent(key, (k, existing) -> {
            Attempt attempt = getCurrentAttempt(existing, now);
            attempt.lastSeen = now;

            if (attempt.inFlight > 0) {
                attempt.inFlight--;
            }

            return attempt.isExpired(now) ? null : attempt;
        });
    }

    private void clear(Map<String, Attempt> attemptsMap, String key) {
        attemptsMap.remove(key);
    }

    private Attempt getCurrentAttempt(Attempt existing, Instant now) {
        if (existing == null || existing.isExpired(now)) {
            return new Attempt(now);
        }

        if (now.isAfter(existing.windowStart.plusSeconds(WINDOW_SECONDS))) {
            existing.resetWindow(now);
        }

        return existing;
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