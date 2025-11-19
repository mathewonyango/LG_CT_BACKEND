package com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class ConversationDto {
    private String id;
    
    @JsonProperty("isGroup")
    private Boolean isGroup;
    
    private String groupName;
    private List<UserStatusDto> participants;
    private MessageDto lastMessage;
    private int unreadCount;
    private LocalDateTime lastActivity;
    private LocalDateTime createdAt;

    //getters and setters
    public String getId() {
        return id;
    }


    public Boolean getIsGroup() {
        return isGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<UserStatusDto> getParticipants() {
        return participants;
    }

    public MessageDto getLastMessage() {
        return lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setIsGroup(Boolean isGroup) {
        this.isGroup = isGroup;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public void setParticipants(List<UserStatusDto> participants) {
        this.participants = participants;
    }
    public void setLastMessage(MessageDto lastMessage) {
        this.lastMessage = lastMessage;
    } 
    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    // Constructor without parameters
    public ConversationDto() {}

   

}
