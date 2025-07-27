package com.livinggoodsbackend.livinggoodsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredDTO {
    private String username;
    private String email;
    private String phoneNumber;


}
