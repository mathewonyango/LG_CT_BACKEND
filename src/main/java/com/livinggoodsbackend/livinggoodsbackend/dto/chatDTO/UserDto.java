package com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO;

import com.livinggoodsbackend.livinggoodsbackend.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Role role; 


}
