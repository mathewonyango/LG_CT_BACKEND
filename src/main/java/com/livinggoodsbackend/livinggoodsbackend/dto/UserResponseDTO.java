package com.livinggoodsbackend.livinggoodsbackend.dto;

import com.livinggoodsbackend.livinggoodsbackend.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String role;

    public void setRole(Role role) {
        this.role = role.name(); // converts enum to string (e.g., CHA, CHP)
    }

    // public void setRole(String name) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'setRole'");
    // }

}
