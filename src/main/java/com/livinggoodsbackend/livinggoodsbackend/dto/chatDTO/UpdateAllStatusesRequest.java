package com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO;

public class UpdateAllStatusesRequest {
    private boolean isOnline;

    // Default constructor
    public UpdateAllStatusesRequest() {}

    // Getters and setters
    public boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }
}