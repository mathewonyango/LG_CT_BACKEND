package com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateStatusRequest {
    @JsonProperty("isOnline")
    private Boolean isOnline;

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

}
