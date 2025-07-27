package com.livinggoodsbackend.livinggoodsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class UserKafkaDTO {
    private String username;
    private String email;
    private String phoneNumber;

    public UserKafkaDTO(String username, String email, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

}
