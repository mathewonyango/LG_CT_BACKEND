package com.livinggoodsbackend.livinggoodsbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommodityRequest {
    @NotBlank(message = "Commodity name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Unit of measure is required")
    private String unitOfMeasure;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
}