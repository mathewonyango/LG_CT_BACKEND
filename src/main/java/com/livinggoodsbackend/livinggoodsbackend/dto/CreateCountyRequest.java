package com.livinggoodsbackend.livinggoodsbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCountyRequest {
    @NotBlank(message = "County name is required")
    private String name;
    
    @NotBlank(message = "County code is required")
    private String code;
}