package com.example.bankcards.dto;

import jakarta.validation.constraints.*;
import java.time.YearMonth;

public record CardCreationRequestDto(
        @NotBlank(message = "Card number must not be blank")
        @Pattern(regexp = "\\d{16}", message = "Card number must be exactly 16 digits")
        String cardNumber,

        @NotBlank(message = "Owner name must not be blank")
        @Size(min = 2, max = 50, message = "Owner name must be between 2 and 50 characters")
        String ownerName,

        @NotNull(message = "Expiry date must not be null")
        @Future(message = "Expiry date must be in the future")
        YearMonth expiry,

        @NotNull(message = "User ID must not be null")
        @Positive(message = "User ID must be positive")
        Long userId
) {}
