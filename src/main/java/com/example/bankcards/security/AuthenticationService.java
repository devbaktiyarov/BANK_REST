package com.example.bankcards.security;

import com.example.bankcards.dto.AuthenticationRequestDto;
import com.example.bankcards.dto.JwtTokenPairDto;
import com.example.bankcards.dto.RefreshJwtTokenDto;

public interface AuthenticationService {
    JwtTokenPairDto login(AuthenticationRequestDto authRequest);
    JwtTokenPairDto refreshTokens(RefreshJwtTokenDto refreshRequest) 
}
