package com.example.flexapp.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_SECONDS = 60;

    private static class Attempt {
        int count;
        Instant windowStart;

        Attempt() {
            this.count = 0;
            this.windowStart = Instant.now();
        }
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public boolean isAllowed(String key) {
        Attempt attempt = attempts.computeIfAbsent(key, k -> new Attempt());

        synchronized (attempt) {
            Instant now = Instant.now();

            if (now.isAfter(attempt.windowStart.plusSeconds(WINDOW_SECONDS))) {
                attempt.count = 0;
                attempt.windowStart = now;
            }

            if (attempt.count >= MAX_ATTEMPTS) {
                return false;
            }

            attempt.count++;
            return true;
        }
    }
}