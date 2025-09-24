package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.NotNull;

public record CardStatusRequestDto(
        @NotNull(message = "Status must not be null")
        CardStatus status
) {}
