package com.example.bankcards.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserManageServiceImpl implements UserManageService {
    
    private final UserRepository userRepository;

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(user -> new UserDto(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        ));
    }
    
}
