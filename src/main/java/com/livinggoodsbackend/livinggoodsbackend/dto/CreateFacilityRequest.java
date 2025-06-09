package com.livinggoodsbackend.livinggoodsbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateFacilityRequest {
    @NotBlank(message = "Facility name is required")
    private String name;
    
    
    
    @NotBlank(message = "Facility type is required")
    private String type;
    
    @NotNull(message = "Ward ID is required")
    private Long wardId;
}