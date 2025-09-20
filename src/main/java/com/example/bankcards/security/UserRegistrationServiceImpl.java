package com.example.bankcards.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.bankcards.dto.JwtTokenPairDto;
import com.example.bankcards.dto.RegistrationRequestDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.ValidationUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public JwtTokenPairDto registerUser(RegistrationRequestDto requestDto) {

        ValidationUtils.validateEmail(requestDto.getEmail());
        ValidationUtils.validatePassword(requestDto.getPassword());

        userRepository.findByEmail(requestDto.getEmail())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("User with this email already exists");
                });

        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRole(Role.ROLE_USER);

        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);

        user.setLastRefreshToken(newRefreshToken);
        userRepository.save(user);

        return new JwtTokenPairDto(newAccessToken, newRefreshToken);
    
    }

}
