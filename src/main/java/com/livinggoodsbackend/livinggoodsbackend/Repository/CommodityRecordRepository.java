package com.livinggoodsbackend.livinggoodsbackend.Repository;

import java.time.LocalDateTime;
import java.util.List;
// import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityRecord;
import com.livinggoodsbackend.livinggoodsbackend.Model.User;
import com.livinggoodsbackend.livinggoodsbackend.dto.DashboardStats.ConsumptionStat;
import com.livinggoodsbackend.livinggoodsbackend.dto.DashboardStats.StockOutStat;
import com.livinggoodsbackend.livinggoodsbackend.dto.ConsumptionProjection;

@Repository
public interface CommodityRecordRepository extends JpaRepository<CommodityRecord, Long> {
    // Basic CRUD operations using UUID instead of Long
    List<CommodityRecord> findByCommunityUnitId(Long communityUnitId);
    List<CommodityRecord> findByCommodityId(Long commodityId);
    List<CommodityRecord> findByCommunityUnitIdAndCommodityId(Long communityUnitId, Long commodityId);
    List<CommodityRecord> findByCreatedBy(User createdBy);
    List<CommodityRecord> findByRecordDateBetween(LocalDateTime start, LocalDateTime end);
    List<CommodityRecord> findByStockOnHandLessThan(Integer threshold);
    
    // Fixed JPQL query for monthly consumption stats
    @Query("""
        SELECT NEW com.livinggoodsbackend.livinggoodsbackend.dto.DashboardStats$ConsumptionStat(
            cu.communityUnitName, 
            COALESCE(SUM(cr.quantityConsumed), 0L)
        )
        FROM CommodityRecord cr
        JOIN cr.communityUnit cu
        WHERE cr.recordDate >= :startDate
        GROUP BY cu.communityUnitName, cu.id
        ORDER BY SUM(cr.quantityConsumed) DESC
    """)
    List<ConsumptionStat> getMonthlyConsumptionStats(@Param("startDate") LocalDateTime startDate);

    // Fixed native query for stock out stats
   @Query(value = """
    SELECT 
        cu.community_unit_name as communityUnitName,
        GROUP_CONCAT(DISTINCT c.name SEPARATOR ', ') as commodityNames
    FROM commodity_records cr
    JOIN community_units cu ON cr.community_unit_id = cu.id
    JOIN commodities c ON cr.commodity_id = c.id
    WHERE cr.stock_on_hand < 5 OR cr.stock_on_hand IS NULL
    GROUP BY cu.id, cu.community_unit_name
""", nativeQuery = true)
List<StockOutStat> getStockOutStats();


    // Removed duplicate method - using projection interface instead
    @Query(value = """
        SELECT 
            cu.community_unit_name as communityUnitName,
            COALESCE(SUM(cr.quantity_consumed), 0) as quantityConsumed
        FROM commodity_records cr
        JOIN community_units cu ON cr.community_unit_id = cu.id
        WHERE cr.record_date >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY)
        GROUP BY cu.id, cu.community_unit_name
        ORDER BY SUM(cr.quantity_consumed) DESC
        LIMIT 3
        """, nativeQuery = true)
    List<ConsumptionProjection> getTopConsumptionStats();
    
    // Additional useful queries for dashboard
    @Query(value = """
        SELECT COUNT(DISTINCT cr.community_unit_id) 
        FROM commodity_records cr
        WHERE cr.stock_on_hand = 0
        """, nativeQuery = true)
    Long countCommunityUnitsWithStockOut();
    
    @Query(value = """
        SELECT COUNT(*) 
        FROM commodity_records cr
        WHERE cr.stock_on_hand <= :threshold
        """, nativeQuery = true)
    Long countLowStockItems(@Param("threshold") Integer threshold);
    
    // Get recent records for a community unit
    @Query("""
        SELECT cr FROM CommodityRecord cr
        WHERE cr.communityUnit.id = :communityUnitId
        ORDER BY cr.recordDate DESC
        """)
    List<CommodityRecord> findRecentRecordsByCommunityUnit(@Param("communityUnitId") Long communityUnitId);
    
    // Get consumption trends
    @Query(value = """
        SELECT 
            DATE(cr.record_date) as recordDate,
            SUM(cr.quantity_consumed) as totalConsumed
        FROM commodity_records cr
        WHERE cr.record_date >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY)
        GROUP BY DATE(cr.record_date)
        ORDER BY DATE(cr.record_date)
        """, nativeQuery = true)
    List<Object[]> getConsumptionTrends();
    
    // Find expired commodities
    @Query("""
        SELECT cr FROM CommodityRecord cr
        WHERE cr.quantityExpired > 0
        ORDER BY cr.recordDate DESC
        """)
    List<CommodityRecord> findRecordsWithExpiredItems();
    
    // Find damaged commodities
    @Query("""
        SELECT cr FROM CommodityRecord cr
        WHERE cr.quantityDamaged > 0
        ORDER BY cr.recordDate DESC
        """)
    List<CommodityRecord> findRecordsWithDamagedItems();

    @Query("SELECT cr FROM CommodityRecord cr " +
           "LEFT JOIN FETCH cr.communityUnit cu " +
           "LEFT JOIN FETCH cu.linkFacility f " +  // Changed from facility to linkFacility
           "LEFT JOIN FETCH f.ward w " +
           "LEFT JOIN FETCH w.subCounty sc " +
           "LEFT JOIN FETCH sc.county")
    List<CommodityRecord> findAllWithLocations();

    @Query("""
    SELECT cr.communityUnit.id, SUM(cr.stockOnHand)
    FROM CommodityRecord cr
    GROUP BY cr.communityUnit.id

""")
List<Object[]> getStockLevelsGroupedByCommunityUnit();

@Query("""
    SELECT COALESCE(SUM(cr.stockOnHand), 0)
    FROM CommodityRecord cr
    WHERE cr.communityUnit.id = :communityUnitId
""")
Integer getTotalStockByCommunityUnitId(@Param("communityUnitId") Long communityUnitId);


}