package com.example.flexapp.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_SECONDS = 60;

    /**
     * Hur länge en post får leva utan aktivitet innan den tas bort helt.
     * Vi låter den leva lite längre än själva rate-limit-fönstret för att
     * undvika onödig churn, men ändå garantera att gamla poster försvinner.
     */
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

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public boolean isAllowed(String key) {
        Instant now = Instant.now();

        Attempt attempt = attempts.compute(key, (k, existing) -> {
            if (existing == null || existing.isExpired(now)) {
                return new Attempt(now);
            }
            return existing;
        });

        synchronized (attempt) {
            if (now.isAfter(attempt.windowStart.plusSeconds(WINDOW_SECONDS))) {
                attempt.resetWindow(now);
            } else {
                attempt.lastSeen = now;
            }

            if (attempt.count >= MAX_ATTEMPTS) {
                return false;
            }

            attempt.count++;
            attempt.lastSeen = now;
            return true;
        }
    }

    @Scheduled(fixedDelay = 60_000)
    public void cleanupExpiredAttempts() {
        Instant now = Instant.now();
        attempts.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
    }
}