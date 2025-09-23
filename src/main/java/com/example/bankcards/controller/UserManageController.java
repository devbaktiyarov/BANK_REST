package com.example.bankcards.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.AuthorizationService;
import com.example.bankcards.service.UserManageService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/users")
public class UserManageController {
    
    private final UserManageService userManageService;
    private final AuthorizationService authorizationService;

    @GetMapping
    public Page<UserDto> getAllUsers(@AuthenticationPrincipal User currentUser, @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        authorizationService.authorizeAdmin(currentUser.getEmail());
        return userManageService.getAllUsers(pageable);
    }

}
