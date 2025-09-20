package com.example.bankcards.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuhorizationServiceImpl implements AuthorizationService {

    private final UserRepository userRepository;

    protected User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->  new AccessDeniedException("User not found"));
    }

    protected boolean hasRole(User user, Role roleName) {
        String roleNameStr = roleName.name();
        return user.getEmail().equals(roleNameStr);
    }

    @Override
    public void authorizeUser(String email) {
        User user = getUserOrThrow(email);
        if (!hasRole(user, Role.ROLE_USER)) {
            throw new AccessDeniedException("Access denied: User role required");
        }

    }

    @Override
    public void authorizaAdmin(String email) {
        User user = getUserOrThrow(email);
        if (!hasRole(user, Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("Access denied: Admin role required");
        }
    }
    
}
