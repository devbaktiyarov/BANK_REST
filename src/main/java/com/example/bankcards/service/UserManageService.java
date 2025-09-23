package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserManageService {
    Page<UserDto> getAllUsers(Pageable pageable);
}
