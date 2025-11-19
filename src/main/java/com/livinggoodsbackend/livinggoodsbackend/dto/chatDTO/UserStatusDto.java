package com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO;
import java.time.Instant;
import java.time.LocalDateTime;
import com.livinggoodsbackend.livinggoodsbackend.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusDto {
    private Long id;
    private String username;
    private String email;
    private Role role; 
    private Boolean isOnline;
    private Instant lastSeen;
     
}
