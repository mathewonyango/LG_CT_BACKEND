package com.livinggoodsbackend.livinggoodsbackend.dto;

import lombok.Data;

@Data
public class SubCountyDTO {
    private Long id;
    private String name;
    private Long countyId;
    private String countyName;
}