package com.example.bankcards.controller;

import com.example.bankcards.dto.JwtTokenPairDto;
import com.example.bankcards.dto.RegistrationRequestDto;
import com.example.bankcards.security.UserRegistrationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api")
public class RegistrationController {
    
    private final UserRegistrationService userRegistrationService;


    @PostMapping("/sign-up")
    public JwtTokenPairDto registerUser(@RequestBody @Valid RegistrationRequestDto requestDto) {
        return userRegistrationService.registerUser(requestDto);
    }
    


}
