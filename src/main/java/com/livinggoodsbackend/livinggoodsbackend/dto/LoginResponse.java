package com.livinggoodsbackend.livinggoodsbackend.dto;

import com.livinggoodsbackend.livinggoodsbackend.enums.Role;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private Long userId;
    private Role role;
}