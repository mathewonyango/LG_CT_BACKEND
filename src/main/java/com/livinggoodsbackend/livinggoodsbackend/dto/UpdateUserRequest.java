package com.livinggoodsbackend.livinggoodsbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.livinggoodsbackend.livinggoodsbackend.enums.Role;

@Data
public class UpdateUserRequest {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private Role role;

    private String currentPassword;
}