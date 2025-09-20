package com.example.bankcards.security;

public interface AuthorizationService {
    void authorizeUser(String email);
    void authorizeAdmin(String email);
}
