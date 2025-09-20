package com.example.bankcards.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserRepository userRepository;

    protected User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }

    protected void requireRole(User user, Role role) {
        if (user.getRole() == null || user.getRole() != role) {
            throw new AccessDeniedException("Access denied: " + role.name() + " required");
        }
    }

    @Override
    public void authorizeUser(String email) {
        requireRole(getUserOrThrow(email), Role.ROLE_USER);
    }

    @Override
    public void authorizeAdmin(String email) {
        requireRole(getUserOrThrow(email), Role.ROLE_ADMIN);
    }

}
