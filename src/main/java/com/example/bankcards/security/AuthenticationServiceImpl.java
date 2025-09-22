package com.example.bankcards.security;

import com.example.bankcards.dto.AuthenticationRequestDto;
import com.example.bankcards.dto.JwtTokenPairDto;
import com.example.bankcards.dto.RefreshJwtTokenDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.ValidationUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public JwtTokenPairDto login(AuthenticationRequestDto authRequest) {
        ValidationUtils.validateEmail(authRequest.email());
        ValidationUtils.validatePassword(authRequest.password());

        User user = userRepository.findByEmail(authRequest.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(authRequest.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        user.setLastRefreshToken(refreshToken);
        userRepository.save(user);

        return new JwtTokenPairDto(accessToken, refreshToken);

    }

    @Override
    public JwtTokenPairDto refreshTokens(RefreshJwtTokenDto refreshRequest) {
        String email = jwtProvider.validateRefreshToken(refreshRequest.refreshToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!refreshRequest.refreshToken().equals(user.getLastRefreshToken())) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        user.setLastRefreshToken(refreshToken);
        userRepository.save(user);

        return new JwtTokenPairDto(accessToken, refreshToken);
    }

}