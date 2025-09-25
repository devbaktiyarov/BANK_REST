package com.example.bankcards.controller;

import com.example.bankcards.security.JwtFilter;
import com.example.bankcards.security.JwtProvider;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
class TestSecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    JwtProvider jwtProvider() {
        return Mockito.mock(JwtProvider.class);
    }

    @Bean
    JwtFilter jwtFilter() {
        return Mockito.mock(JwtFilter.class);
    }
}
