package com.example.flexapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminChangePasswordRequest {

    @NotBlank(message = "New password is required")
    @Size(min = 12, message = "Password must be at least 12 characters long")
    private String newPassword;
}
