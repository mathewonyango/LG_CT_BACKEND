package com.livinggoodsbackend.livinggoodsbackend.dto;

import java.time.LocalDateTime;
import lombok.Data;
import com.livinggoodsbackend.livinggoodsbackend.enums.Role;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}