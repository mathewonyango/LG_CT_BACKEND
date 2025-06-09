package com.livinggoodsbackend.livinggoodsbackend.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CommodityDTO {
    private Long id;
    private String name;
    private String description;
    private String unitOfMeasure;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private Long createdById;
    private String createdByUsername;
}