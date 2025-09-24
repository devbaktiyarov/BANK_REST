package com.example.bankcards.dto;

import java.math.BigDecimal;

public record CardDto(Long id, String cardNumber, String ownerName, String expiry, String status, BigDecimal balance,
        Long userId) {
}
