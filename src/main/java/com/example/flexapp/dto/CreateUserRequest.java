package com.example.flexapp.dto;

import com.example.flexapp.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
    private boolean active;
}
