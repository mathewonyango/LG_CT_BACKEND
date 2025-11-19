package com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadByDto {
    private Long userId;
    private LocalDateTime readAt;
}
