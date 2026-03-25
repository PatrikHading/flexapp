package com.example.flexapp.controller;

import com.example.flexapp.dto.AdminUpdateUserRequest;
import com.example.flexapp.dto.CreateUserRequest;
import com.example.flexapp.dto.UserProfileResponse;
import com.example.flexapp.service.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public List<UserProfileResponse> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    @PostMapping
    public UserProfileResponse createUser(@RequestBody CreateUserRequest request) {
        return adminUserService.createUser(request);
    }

    @PutMapping("/{id}")
    public UserProfileResponse updateUser(@PathVariable Long id,
                                          @RequestBody AdminUpdateUserRequest request) {
        return adminUserService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        adminUserService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}