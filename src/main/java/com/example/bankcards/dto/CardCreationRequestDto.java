package com.example.bankcards.dto;

import java.time.YearMonth;

public record CardCreationRequestDto(String cardNumber, String ownerName, YearMonth expiry, Long userId) {}
