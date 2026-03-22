package com.example.flexapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())          // Tillåt POST utan CSRF-token
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()      // Tillåt alla requests
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}