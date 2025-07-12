package com.livinggoodsbackend.livinggoodsbackend.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ChpDashboardStatsDTO {
    private int totalRecords;
    private int totalIssued;
    private int totalConsumed;
    private int totalExpired;
    private int totalDamaged;
    private int totalOutOfStock;
    private List<String> commoditiesToReorder;
    private List<String> commoditiesInExcess;
    private List<String> slowMovingCommodities;
    private List<String> outOfStockCommodities;
    private String advice;
    private Map<String, Double> forecast;

}