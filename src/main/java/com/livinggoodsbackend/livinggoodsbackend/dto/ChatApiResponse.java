package com.livinggoodsbackend.livinggoodsbackend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    public static <T> ChatApiResponse<T> success(String message, T data) {
        return new ChatApiResponse<>(true, message, data);
    }
    
    public static <T> ChatApiResponse<T> error(String message) {
        return new ChatApiResponse<>(false, message, null);
    }
}
