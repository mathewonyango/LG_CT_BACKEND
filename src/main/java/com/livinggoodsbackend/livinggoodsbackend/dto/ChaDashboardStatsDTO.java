package com.livinggoodsbackend.livinggoodsbackend.dto;

import lombok.Data;

@Data
public class ChaDashboardStatsDTO {
    private int totalRecords;
    private int totalIssued;
    private int totalConsumed;
    private int totalExpired;
    private int totalDamaged;
    private int totalClosingBalance;
    private ChpDashboardStatsDTO stats; // <-- Add this field

    // Add more fields as needed
}