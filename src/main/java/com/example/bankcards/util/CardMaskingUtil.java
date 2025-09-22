package com.example.bankcards.util;

public class CardMaskingUtil {
    private CardMaskingUtil() {}

    public static String mask(String plainNumber) {
        if (plainNumber == null || plainNumber.length() < 4) return "****";
        String last4 = plainNumber.substring(plainNumber.length() - 4);
        return "**** **** **** " + last4;
    }
}
