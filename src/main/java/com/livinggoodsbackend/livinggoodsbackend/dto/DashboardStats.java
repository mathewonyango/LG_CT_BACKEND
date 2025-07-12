package com.livinggoodsbackend.livinggoodsbackend.dto;

import java.util.List;

public class DashboardStats {

    // Entity counts
    private int totalCounties;
    private int totalSubCounties;
    private int totalWards;
    private int totalFacilities;
    private int totalCommunityUnits;

    // Stock & alert metrics
    private Long totalStockOuts;
    private Long lowStockItems;

    // Statistics
    private List<ConsumptionStat> monthlyConsumption;
    private List<StockOutStat> stockOutStats;
    private List<ConsumptionStat> topConsumption;

    // === Getters and Setters ===

    public int getTotalCounties() {
        return totalCounties;
    }

    public void setTotalCounties(int totalCounties) {
        this.totalCounties = totalCounties;
    }

    public int getTotalSubCounties() {
        return totalSubCounties;
    }

    public void setTotalSubCounties(int totalSubCounties) {
        this.totalSubCounties = totalSubCounties;
    }

    public int getTotalWards() {
        return totalWards;
    }

    public void setTotalWards(int totalWards) {
        this.totalWards = totalWards;
    }

    public int getTotalFacilities() {
        return totalFacilities;
    }

    public void setTotalFacilities(int totalFacilities) {
        this.totalFacilities = totalFacilities;
    }

    public int getTotalCommunityUnits() {
        return totalCommunityUnits;
    }

    public void setTotalCommunityUnits(int totalCommunityUnits) {
        this.totalCommunityUnits = totalCommunityUnits;
    }

    public Long getTotalStockOuts() {
        return totalStockOuts;
    }

    public void setTotalStockOuts(Long totalStockOuts) {
        this.totalStockOuts = totalStockOuts;
    }

    public Long getLowStockItems() {
        return lowStockItems;
    }

    public void setLowStockItems(Long lowStockItems) {
        this.lowStockItems = lowStockItems;
    }

    public List<ConsumptionStat> getMonthlyConsumption() {
        return monthlyConsumption;
    }

    public void setMonthlyConsumption(List<ConsumptionStat> monthlyConsumption) {
        this.monthlyConsumption = monthlyConsumption;
    }

    public List<StockOutStat> getStockOutStats() {
        return stockOutStats;
    }

    public void setStockOutStats(List<StockOutStat> stockOutStats) {
        this.stockOutStats = stockOutStats;
    }

    public List<ConsumptionStat> getTopConsumption() {
        return topConsumption;
    }

    public void setTopConsumption(List<ConsumptionStat> topConsumption) {
        this.topConsumption = topConsumption;
    }

    // === Nested DTO: ConsumptionStat ===
    public static class ConsumptionStat {
        private String name;          // Community Unit Name
        private Long consumption;     // Total Quantity Consumed

        public ConsumptionStat() {}

        public ConsumptionStat(String name, Long consumption) {
            this.name = name;
            this.consumption = consumption;
        }
    public ConsumptionStat(String name, Integer consumption) {
    this.name = name;
    this.consumption = consumption != null ? consumption.longValue() : 0L;
}


        public String getName() {
            return name;
        }


        public void setName(String name) {
            this.name = name;
        }

        public Long getConsumption() {
            return consumption;
        }

        public void setConsumption(Long consumption) {
            this.consumption = consumption;
        }
    }

    // === Nested DTO: StockOutStat ===
    public static class StockOutStat {
        private String communityUnitName;
        private String commodityNames;

        public StockOutStat() {}

        public StockOutStat(String communityUnitName, String commodityNames) {
            this.communityUnitName = communityUnitName;
            this.commodityNames = commodityNames;
        }

        public String getCommunityUnitName() {
            return communityUnitName;
        }

        public void setCommunityUnitName(String communityUnitName) {
            this.communityUnitName = communityUnitName;
        }

        public String getCommodityNames() {
            return commodityNames;
        }

        public void setCommodityNames(String commodityNames) {
            this.commodityNames = commodityNames;
        }
    }
}
