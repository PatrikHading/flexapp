package com.example.flexapp.controller;

import com.example.flexapp.dto.UserProfileResponse;
import com.example.flexapp.service.AdminUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}