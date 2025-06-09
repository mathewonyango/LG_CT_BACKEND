package com.livinggoodsbackend.livinggoodsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DropdownResponse {
    private Long id;
    private String name;
    private Long parentId;  // This will store countyId for subCounties and subCountyId for wards
    
    // Constructor without parentId for counties
    public DropdownResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}