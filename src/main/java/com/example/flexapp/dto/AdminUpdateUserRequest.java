package com.example.flexapp.dto;

import com.example.flexapp.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateUserRequest {

    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private boolean active;

}
