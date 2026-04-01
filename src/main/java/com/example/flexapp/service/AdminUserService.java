package com.example.flexapp.service;

import com.example.flexapp.dto.AdminChangePasswordRequest;
import com.example.flexapp.dto.AdminUpdateUserRequest;
import com.example.flexapp.dto.CreateUserRequest;
import com.example.flexapp.dto.UserProfileResponse;
import com.example.flexapp.entity.User;
import com.example.flexapp.exception.AccessDeniedException;
import com.example.flexapp.exception.BadRequestException;
import com.example.flexapp.exception.ResourceNotFoundException;
import com.example.flexapp.repository.UserRepository;
import com.example.flexapp.security.SecurityService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(UserRepository userRepository,
                            SecurityService securityService,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserProfileResponse> getAllUsers() {
        requireAdmin();

        return userRepository.findAll()
                .stream()
                .map(this::toUserProfileResponse)
                .toList();
    }

    public UserProfileResponse createUser(CreateUserRequest request) {
        requireAdmin();

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email is already in use.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setActive(request.isActive());

        User savedUser = userRepository.save(user);
        return toUserProfileResponse(savedUser);
    }

    public UserProfileResponse updateUser(Long userId, AdminUpdateUserRequest request) {
        requireAdmin();

        User currentUser = securityService.getCurrentUser();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        validateNoSelfLockoutOnUpdate(currentUser, user, request);

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (!user.getEmail().equalsIgnoreCase(normalizedEmail)
                && userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email is already in use.");
        }

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(normalizedEmail);
        user.setRole(request.getRole());
        user.setActive(request.isActive());

        User savedUser = userRepository.save(user);
        return toUserProfileResponse(savedUser);
    }

    public void changeUserPassword(Long userId, AdminChangePasswordRequest request) {
        requireAdmin();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from the current password.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.incrementTokenVersion();
        userRepository.save(user);
    }

    public void deactivateUser(Long userId) {
        requireAdmin();

        User currentUser = securityService.getCurrentUser();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        validateNoSelfDeactivate(currentUser, user);

        user.setActive(false);
        user.incrementTokenVersion();
        userRepository.save(user);
    }

    private void requireAdmin() {
        if (!securityService.isAdmin()) {
            throw new AccessDeniedException("Only admins can manage users.");
        }
    }

    private void validateNoSelfLockoutOnUpdate(User currentUser, User targetUser, AdminUpdateUserRequest request) {
        if (!currentUser.getId().equals(targetUser.getId())) {
            return;
        }

        if (!request.isActive()) {
            throw new AccessDeniedException("Admins cannot deactivate their own account.");
        }

        if (request.getRole() != currentUser.getRole()) {
            throw new AccessDeniedException("Admins cannot change their own role.");
        }
    }

    private void validateNoSelfDeactivate(User currentUser, User targetUser) {
        if (currentUser.getId().equals(targetUser.getId())) {
            throw new AccessDeniedException("Admins cannot deactivate their own account.");
        }
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