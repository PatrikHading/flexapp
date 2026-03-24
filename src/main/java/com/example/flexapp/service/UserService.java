package com.example.flexapp.service;

import com.example.flexapp.dto.ChangePasswordRequest;
import com.example.flexapp.dto.UserProfileResponse;
import com.example.flexapp.entity.User;
import com.example.flexapp.exception.BadRequestException;
import com.example.flexapp.repository.UserRepository;
import com.example.flexapp.security.SecurityService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       SecurityService securityService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileResponse getCurrentUserProfile() {
        User currentUser = securityService.getCurrentUser();
        return toProfileResponse(currentUser);
    }

    public void changePassword(ChangePasswordRequest request) {
        User currentUser = securityService.getCurrentUser();

        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
            throw new BadRequestException("Current password is required.");
        }

        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new BadRequestException("New password is required.");
        }

        if (request.getNewPassword().length() < 6) {
            throw new BadRequestException("New password must be at least 6 characters long.");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new BadRequestException("Current password is incorrect.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), currentUser.getPassword())) {
            throw new BadRequestException("New password must be different from the current password.");
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);

    }

    private UserProfileResponse toProfileResponse(User user) {
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
