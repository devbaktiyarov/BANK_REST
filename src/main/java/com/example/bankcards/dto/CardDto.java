package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.YearMonth;

public record CardDto(
        Long id,
        String cardNumber,
        String ownerName,
        YearMonth expiry,
        String status,
        BigDecimal balance,
        Long userId
) {}
