package com.example.bankcards.security;

import com.example.bankcards.dto.JwtTokenPairDto;
import com.example.bankcards.dto.RegistrationRequestDto;

public interface UserRegistrationService {
    JwtTokenPairDto registerUser(RegistrationRequestDto requestDto);
}
