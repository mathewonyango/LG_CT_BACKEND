package com.livinggoodsbackend.livinggoodsbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateFacilityRequest {
    @NotBlank(message = "Facility name is required")
    private String name;
    
    
    
    @NotBlank(message = "Facility type is required")
    private String type;
    
    @NotNull(message = "Ward ID is required")
    private List<Long> wardIds;

    public String getFacilityCode() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFacilityCode'");
    }
}