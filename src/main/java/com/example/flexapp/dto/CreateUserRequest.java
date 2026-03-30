package com.example.flexapp.dto;

import com.example.flexapp.enums.Role;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank(message = "First name is required.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    private String lastName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be a valid email address.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 12, message = "Password must be at least 12 characters long.")
    private String password;

    @NotNull(message = "Role is required.")
    private Role role;

    @NotNull(message = "Active status is required.")
    private boolean active;
}
