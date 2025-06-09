package com.livinggoodsbackend.livinggoodsbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class LocationDropdownData {
    private List<DropdownResponse> counties;
    private List<DropdownResponse> subCounties;
    private List<DropdownResponse> wards;
    private List<DropdownResponse> facilities;
    private List<DropdownResponse> communityUnits;
    private List<DropdownResponse> communityHealthWorkers;
}