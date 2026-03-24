package com.example.flexapp.service;

import com.example.flexapp.dto.UserProfileResponse;
import com.example.flexapp.entity.User;
import com.example.flexapp.exception.AccessDeniedException;
import com.example.flexapp.repository.UserRepository;
import com.example.flexapp.security.SecurityService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final SecurityService securityService;

    public AdminUserService(UserRepository userRepository,
                            SecurityService securityService) {
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    public List<UserProfileResponse> getAllUsers() {
        if (!securityService.isAdmin()) {
            throw new AccessDeniedException("Only admins can access all users.");
        }

        return userRepository.findAll()
                .stream()
                .map(this::toUserProfileResponse)
                .toList();
    }

    private UserProfileResponse toUserProfileResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.isActive()
        );
    }
}