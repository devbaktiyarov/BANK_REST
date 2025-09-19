package com.example.bankcards.util;

public class ValidationUtils {

    private ValidationUtils() {
    }

    public static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
    }

}

