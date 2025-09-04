package com.email.email.writer.controller;

import com.email.email.writer.model.EmailRequest;
import com.email.email.writer.service.EmailGeneratorService;
import com.email.email.writer.service.RateLimiterService;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class EmailGeneratorController {

    private final EmailGeneratorService emailGeneratorService;
    private final RateLimiterService rateLimiterService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest) {
        // ✅ Get logged-in username from Spring Security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";

        // ✅ Get per-user limiter
        RateLimiter limiter = rateLimiterService.getEmailLimiter(username);

        // ✅ Wrap the service call inside rate limiter
        Supplier<ResponseEntity<String>> restrictedCall =
                RateLimiter.decorateSupplier(limiter, () -> {
                    String response = emailGeneratorService.generateEmailReply(emailRequest);
                    return ResponseEntity.ok(response);
                });

        try {
            return restrictedCall.get();
        } catch (RequestNotPermitted ex) {
            return ResponseEntity.status(429).body("Too many requests. Please try again later.");
        }
    }
}
