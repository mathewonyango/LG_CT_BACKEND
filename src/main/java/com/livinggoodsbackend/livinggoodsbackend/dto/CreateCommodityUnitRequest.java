package com.livinggoodsbackend.livinggoodsbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CreateCommodityUnitRequest {
    @NotBlank(message = "CHA name is required")
    private String chaName;

    @NotBlank(message = "Community unit name is required")
    private String communityUnitName;

    @NotNull(message = "Total CHPs is required")
    @Min(value = 1, message = "Total CHPs must be at least 1")
    private Integer totalChps;

    @NotNull(message = "County ID is required")
    private Long countyId;

    @NotNull(message = "Sub-county ID is required")
    private Long subCountyId;

    @NotNull(message = "Ward ID is required")
    private Long wardId;

    private Long linkFacilityId;
    
    private Long createdBy;
    @NotNull(message = "Total CHPs counted is required")
    private Integer totalCHPsCounted;
}