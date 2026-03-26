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

        validateCreateUserRequest(request);

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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        validateAdminUpdateRequest(request);

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

        validateAdminChangePasswordRequest(request);

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from the current password.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void deactivateUser(Long userId) {
        requireAdmin();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setActive(false);
        userRepository.save(user);
    }

    private void requireAdmin() {
        if (!securityService.isAdmin()) {
            throw new AccessDeniedException("Only admins can manage users.");
        }
    }

    private void validateCreateUserRequest(CreateUserRequest request) {
        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            throw new BadRequestException("First name is required.");
        }

        if (request.getLastName() == null || request.getLastName().isBlank()) {
            throw new BadRequestException("Last name is required.");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email is required.");
        }

        if (!request.getEmail().contains("@")) {
            throw new BadRequestException("Email must be valid.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Password is required.");
        }

        if (request.getPassword().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long.");
        }

        if (request.getRole() == null) {
            throw new BadRequestException("Role is required.");
        }
    }

    private void validateAdminUpdateRequest(AdminUpdateUserRequest request) {
        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            throw new BadRequestException("First name is required.");
        }

        if (request.getLastName() == null || request.getLastName().isBlank()) {
            throw new BadRequestException("Last name is required.");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email is required.");
        }

        if (!request.getEmail().contains("@")) {
            throw new BadRequestException("Email must be valid.");
        }

        if (request.getRole() == null) {
            throw new BadRequestException("Role is required.");
        }
    }

    private void validateAdminChangePasswordRequest(AdminChangePasswordRequest request) {
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new BadRequestException("New password is required.");
        }

        if (request.getNewPassword().length() < 6) {
            throw new BadRequestException("New password must be at least 6 characters long.");
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