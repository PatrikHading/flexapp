package com.example.flexapp.service;

import com.example.flexapp.dto.ChangePasswordRequest;
import com.example.flexapp.dto.UpdateProfileRequest;
import com.example.flexapp.dto.UserProfileResponse;
import com.example.flexapp.entity.User;
import com.example.flexapp.exception.BadRequestException;
import com.example.flexapp.repository.UserRepository;
import com.example.flexapp.security.SecurityService;
import org.springframework.dao.DataIntegrityViolationException;
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

    public UserProfileResponse updateCurrentUserProfile(UpdateProfileRequest request) {
        User currentUser = securityService.getCurrentUser();

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (!currentUser.getEmail().equalsIgnoreCase(normalizedEmail)
                && userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email is already in use.");
        }

        currentUser.setFirstName(request.getFirstName().trim());
        currentUser.setLastName(request.getLastName().trim());
        currentUser.setEmail(normalizedEmail);

        User savedUser = saveUserHandlingDuplicateEmail(currentUser);
        return toProfileResponse(savedUser);
    }

    public void changePassword(ChangePasswordRequest request) {
        User currentUser = securityService.getCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new BadRequestException("Current password is incorrect.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), currentUser.getPassword())) {
            throw new BadRequestException("New password must be different from the current password.");
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        currentUser.incrementTokenVersion();
        userRepository.save(currentUser);
    }

    public void invalidateCurrentUserSessions() {
        User currentUser = securityService.getCurrentUser();
        currentUser.incrementTokenVersion();
        userRepository.save(currentUser);
    }

    private User saveUserHandlingDuplicateEmail(User user) {
        try {
            return userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Email is already in use.");
        }
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