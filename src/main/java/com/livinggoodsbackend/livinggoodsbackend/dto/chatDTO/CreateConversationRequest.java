package com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CreateConversationRequest {
    private List<Long> participantIds;
    
    @JsonProperty("isGroup")
    private Boolean isGroup;
    
    private String groupName;
   
    
}