package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshJwtTokenDto(
        @NotBlank(message = "Refresh token must not be blank")
        String refreshToken
) {}
