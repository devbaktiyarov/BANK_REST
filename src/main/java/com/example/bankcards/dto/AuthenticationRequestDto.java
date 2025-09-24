package com.example.bankcards.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationRequestDto(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 4, max = 50, message = "Password must be between 6 and 50 characters long")
        String password
) {}

