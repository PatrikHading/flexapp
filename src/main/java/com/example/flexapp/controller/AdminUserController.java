package com.example.flexapp.controller;

import com.example.flexapp.dto.AdminChangePasswordRequest;
import com.example.flexapp.dto.AdminUpdateUserRequest;
import com.example.flexapp.dto.CreateUserRequest;
import com.example.flexapp.dto.ManualTimeEntryRequest;
import com.example.flexapp.dto.TimeEntryResponse;
import com.example.flexapp.dto.UserProfileResponse;
import com.example.flexapp.service.AdminUserService;
import com.example.flexapp.service.TimeEntryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final TimeEntryService timeEntryService;

    public AdminUserController(AdminUserService adminUserService,
                               TimeEntryService timeEntryService) {
        this.adminUserService = adminUserService;
        this.timeEntryService = timeEntryService;
    }

    @GetMapping
    public List<UserProfileResponse> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    @PostMapping
    public UserProfileResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return adminUserService.createUser(request);
    }

    @PutMapping("/{id}")
    public UserProfileResponse updateUser(@PathVariable Long id,
                                          @Valid @RequestBody AdminUpdateUserRequest request) {
        return adminUserService.updateUser(id, request);
    }

    @PostMapping("/{id}/time/manual")
    public TimeEntryResponse registerManualEntryForUser(@PathVariable Long id,
                                                        @Valid @RequestBody ManualTimeEntryRequest request) {
        return timeEntryService.registerManualEntryAsAdmin(id, request);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changeUserPassword(@PathVariable Long id,
                                                   @Valid @RequestBody AdminChangePasswordRequest request) {
        adminUserService.changeUserPassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        adminUserService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}