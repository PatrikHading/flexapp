package com.example.flexapp.controller;

import com.example.flexapp.dto.LoginRequest;
import com.example.flexapp.entity.User;
import com.example.flexapp.security.LoginRateLimiter;
import com.example.flexapp.service.JwtService;
import com.example.flexapp.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final LoginRateLimiter rateLimiter;

    @Value("${cookie.secure:false}")
    private boolean cookieSecure;

    public AuthController(AuthenticationManager authManager,
                          JwtService jwtService,
                          UserService userService,
                          LoginRateLimiter rateLimiter) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.rateLimiter = rateLimiter;
    }

    @GetMapping("/csrf")
    public ResponseEntity<CsrfTokenResponse> csrf(CsrfToken csrfToken) {
        return ResponseEntity.ok(new CsrfTokenResponse(csrfToken.getToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request,
                                      HttpServletRequest httpRequest,
                                      HttpServletResponse response) {

        String clientIp = extractClientIp(httpRequest);

        if (!rateLimiter.isAllowed(clientIp)) {
            return ResponseEntity.status(429).build();
        }

        var authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 8);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        userService.invalidateCurrentUserSessions();

        Cookie cookie = new Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0];
        }

        return request.getRemoteAddr();
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