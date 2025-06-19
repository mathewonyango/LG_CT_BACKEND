package com.livinggoodsbackend.livinggoodsbackend.dto;

import java.time.LocalDateTime;

import com.livinggoodsbackend.livinggoodsbackend.Model.County;
import com.livinggoodsbackend.livinggoodsbackend.Model.Facility;

import lombok.Data;

@Data
public class CommodityUnitDTO {
    private Long id;
    private String chaName;
    private String communityUnitName;
    private Integer totalChps;
    private Long countyId;
    private Long subCountyId;
    private Long wardId;
    private Long linkFacilityId;
    private Integer createdBy;
    // private String createdByUsername;
    private LocalDateTime createdAt;
    // private String wardName;
    // private String subCountyName;
    // private String countyName;
    private Integer stockLevel; // Add this

    private Integer totalCHPsCounted; // Should be Integer
}