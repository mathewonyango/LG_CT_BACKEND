package com.livinggoodsbackend.livinggoodsbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class FacilityDTO {
    private String name;
    private String type;
    private String facilityCode;
    private List<Long> wardIds;
}