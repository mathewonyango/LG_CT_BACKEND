package com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkReadResponse {
    private int markedCount;
    private List<String> messageIds;
}
