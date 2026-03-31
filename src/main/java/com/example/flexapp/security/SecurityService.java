package com.example.flexapp.security;

import com.example.flexapp.entity.User;
import com.example.flexapp.enums.Role;
import com.example.flexapp.exception.AccessDeniedException;
import com.example.flexapp.exception.ResourceNotFoundException;
import com.example.flexapp.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final UserRepository userRepository;

    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("No authenticated user found.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user;
        }

        String email = authentication.getName();
        if (email == null || email.isBlank()) {
            throw new AccessDeniedException("No authenticated user found.");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found: " + email
                ));
    }

    public void validateUserAccess(Long requestedUserId) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        if (!currentUser.getId().equals(requestedUserId)) {
            throw new AccessDeniedException("You are not allowed to access another user's data.");
        }
    }

    public boolean isAdmin() {
        return getCurrentUser().getRole() == Role.ADMIN;
    }

    public void validateAdminAccess() {
        if (!isAdmin()) {
            throw new AccessDeniedException("Only admins can perform this action.");
        }
    }
}