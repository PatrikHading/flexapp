package com.example.flexapp.controller;

import com.example.flexapp.dto.LoginRequest;
import com.example.flexapp.entity.User;
import com.example.flexapp.security.ClientIpResolver;
import com.example.flexapp.security.LoginRateLimiter;
import com.example.flexapp.service.JwtService;
import com.example.flexapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final LoginRateLimiter rateLimiter;
    private final ClientIpResolver clientIpResolver;

    @Value("${cookie.secure:false}")
    private boolean cookieSecure;

    public AuthController(AuthenticationManager authManager,
                          JwtService jwtService,
                          UserService userService,
                          LoginRateLimiter rateLimiter,
                          ClientIpResolver clientIpResolver) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.rateLimiter = rateLimiter;
        this.clientIpResolver = clientIpResolver;
    }

    @GetMapping("/csrf")
    public ResponseEntity<CsrfTokenResponse> csrf(CsrfToken csrfToken) {
        return ResponseEntity.ok(new CsrfTokenResponse(csrfToken.getToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request,
                                      HttpServletRequest httpRequest,
                                      HttpServletResponse response) {

        String clientIp = clientIpResolver.resolveClientIp(httpRequest);
        String email = request.getEmail();

        if (!rateLimiter.tryAcquire(clientIp, email)) {
            return ResponseEntity.status(429).build();
        }

        try {
            var authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtService.generateToken(user);

            rateLimiter.recordSuccess(clientIp, email);

            String cookieValue = String.format(
                    "jwt=%s; Path=/; Max-Age=%d; HttpOnly; %s SameSite=Strict",
                    token,
                    60 * 60 * 8,
                    cookieSecure ? "Secure;" : ""
            );

            response.addHeader("Set-Cookie", cookieValue);

            return ResponseEntity.ok().build();

        } catch (AuthenticationException ex) {
            rateLimiter.recordFailure(clientIp, email);
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        userService.invalidateCurrentUserSessions();

        String cookieValue = String.format(
                "jwt=; Path=/; Max-Age=0; HttpOnly; %s SameSite=Strict",
                cookieSecure ? "Secure;" : ""
        );

        response.addHeader("Set-Cookie", cookieValue);

        return ResponseEntity.ok().build();
    }

    public static class CsrfTokenResponse {
        private final String token;

        public CsrfTokenResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }
}