package com.livinggoodsbackend.livinggoodsbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChaDashboardResponseDTO {
    private List<ChpDashboardDTO> chps;
    private ChaDashboardStatsDTO stats;
    private String advice; // <-- Add this field
}