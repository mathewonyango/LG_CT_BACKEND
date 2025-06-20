package com.livinggoodsbackend.livinggoodsbackend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.livinggoodsbackend.livinggoodsbackend.Repository.*;
import com.livinggoodsbackend.livinggoodsbackend.dto.DashboardStats;
import com.livinggoodsbackend.livinggoodsbackend.dto.DashboardStats.ConsumptionStat;
import com.livinggoodsbackend.livinggoodsbackend.dto.DashboardStats.StockOutStat;
import com.livinggoodsbackend.livinggoodsbackend.dto.ConsumptionProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private CountyRepository countyRepository;

    @Autowired
    private SubCountyRepository subCountyRepository;

    @Autowired
    private WardRepository wardRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private CommodityUnitRepository commodityUnitRepository; // This represents community units

    @Autowired
    private CommodityRecordRepository commodityRecordRepository;

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        // Set entity counts
        stats.setTotalCounties((int) countyRepository.count());
        stats.setTotalSubCounties((int) subCountyRepository.count());
        stats.setTotalWards((int) wardRepository.count());
        stats.setTotalFacilities((int) facilityRepository.count());
        stats.setTotalCommunityUnits((int) commodityUnitRepository.count());

        // Last 30 days data
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // Monthly consumption
        List<ConsumptionStat> monthlyConsumption = commodityRecordRepository.getMonthlyConsumptionStats(thirtyDaysAgo);
        stats.setMonthlyConsumption(monthlyConsumption);

        // Stock out
        List<StockOutStat> stockOutStats = commodityRecordRepository.getStockOutStats();
        stats.setStockOutStats(stockOutStats);

        // Top consumption
        List<ConsumptionProjection> topConsumptionData = commodityRecordRepository.getTopConsumptionStats();
        List<ConsumptionStat> topConsumptionStats = topConsumptionData.stream()
                .map(p -> new ConsumptionStat(p.getCommunityUnitName(), p.getQuantityConsumed()))
                .collect(Collectors.toList());
        stats.setTopConsumption(topConsumptionStats);

        // Stock metrics
        stats.setTotalStockOuts(commodityRecordRepository.countCommunityUnitsWithStockOut());
        stats.setLowStockItems(commodityRecordRepository.countLowStockItems(0)); // threshold of 10

        return stats;
    }

    // Separate data points
    public List<Object[]> getConsumptionTrends() {
        return commodityRecordRepository.getConsumptionTrends();
    }

    public List<ConsumptionStat> getMonthlyConsumption() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return commodityRecordRepository.getMonthlyConsumptionStats(thirtyDaysAgo);
    }

    public List<StockOutStat> getStockOutUnits() {
        return commodityRecordRepository.getStockOutStats();
    }

    public Long getTotalStockOuts() {
        return commodityRecordRepository.countCommunityUnitsWithStockOut();
    }

    public Long getLowStockCount(Integer threshold) {
        return commodityRecordRepository.countLowStockItems(threshold != null ? threshold : 10);
    }
}
