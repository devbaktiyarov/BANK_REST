package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthenticationRequestDto;
import com.example.bankcards.dto.JwtTokenPairDto;
import com.example.bankcards.dto.RefreshJwtTokenDto;
import com.example.bankcards.security.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api")
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<JwtTokenPairDto> login(@RequestBody @Valid AuthenticationRequestDto authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtTokenPairDto> getNewRefreshToken(@RequestBody @Valid RefreshJwtTokenDto jwtRequestDto) {
        return ResponseEntity.ok(authService.refreshTokens(jwtRequestDto));
    }

}
