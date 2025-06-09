package com.livinggoodsbackend.livinggoodsbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSubCountyRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "County ID is required")
    private Long countyId;
}