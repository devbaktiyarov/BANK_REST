package com.example.bankcards.dto;

public record CardDto(Long id, String cardNumber, String ownerName, String expiry, String status, Long userId) {}
