package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TransferRequestDto(
        @NotNull(message = "From card ID must not be null")
        @Positive(message = "From card ID must be positive")
        Long fromCardId,

        @NotNull(message = "To card ID must not be null")
        @Positive(message = "To card ID must be positive")
        Long toCardId,

        @NotNull(message = "Amount must not be null")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount
) {}
