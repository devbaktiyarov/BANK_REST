package com.example.bankcards.security;

public interface AuthorizationService {
    void authorizeUser(String email);
    void authorizaAdmin(String email);
}
