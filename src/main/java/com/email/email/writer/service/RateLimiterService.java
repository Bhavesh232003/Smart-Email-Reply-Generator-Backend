package com.email.email.writer.service;


import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final ConcurrentHashMap<String, RateLimiter> loginLimiters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RateLimiter> emailLimiters = new ConcurrentHashMap<>();

    // Get or create per-user login limiter (10/day)
    public RateLimiter getLoginLimiter(String username) {
        return loginLimiters.computeIfAbsent(username, user ->
            RateLimiter.of("loginApi-" + user,
                RateLimiterConfig.custom()
                    .limitForPeriod(10)
                    .limitRefreshPeriod(Duration.ofDays(1))
                    .timeoutDuration(Duration.ZERO)
                    .build()
            )
        );
    }

    // Get or create per-user email limiter (4/minute)
    public RateLimiter getEmailLimiter(String username) {
        return emailLimiters.computeIfAbsent(username, user ->
            RateLimiter.of("emailApi-" + user,
                RateLimiterConfig.custom()
                    .limitForPeriod(4)
                    .limitRefreshPeriod(Duration.ofMinutes(1))
                    .timeoutDuration(Duration.ZERO)
                    .build()
            )
        );
    }
}
