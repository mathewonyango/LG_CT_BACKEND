package com.livinggoodsbackend.livinggoodsbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChpDashboardDTO {
    private Long chpId;
    private String chpUsername;
    private String chpEmail;
    private String phoneNumber;
    private List<CommodityRecordDTO> commodityRecords;
    private ChpDashboardStatsDTO stats; // <-- Add this field
}