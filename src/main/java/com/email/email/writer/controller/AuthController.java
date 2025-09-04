package com.email.email.writer.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.email.email.writer.dto.LoginRequest;
import com.email.email.writer.dto.LoginResponse;
import com.email.email.writer.jwt.JwtUtils;
import com.email.email.writer.model.User;
import com.email.email.writer.service.RateLimiterService;
import com.email.email.writer.service.UserService;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;

//import com.example.Securitydemo.jwt.LoginResponse;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Allow all origins (you can specify specific domains)

public class AuthController {
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JwtUtils jwtUtils;
	
	 @Autowired
	 private RateLimiterService rateLimiterService;
	
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();

        // Per-user limiter (10 logins/day)
        RateLimiter limiter = rateLimiterService.getLoginLimiter(username);

        try {
            return RateLimiter.decorateSupplier(limiter, () -> {
                try {
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword())
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

                    LoginResponse response = new LoginResponse(jwtToken, userDetails.getUsername());
                    return ResponseEntity.ok(response);
                } catch (AuthenticationException e) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                            Map.of("message", "Invalid credentials", "status", false)
                    );
                }
            }).get();
        } catch (RequestNotPermitted ex) {
            return ResponseEntity.status(429).body("Login limit exceeded. Max 10 logins per day.");
        }
    }

}
