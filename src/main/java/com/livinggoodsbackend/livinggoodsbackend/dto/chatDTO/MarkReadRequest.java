package com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkReadRequest {
    private Long userId;
    private List<String> messageIds;
}
