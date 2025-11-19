package com.livinggoodsbackend.livinggoodsbackend.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
    // Basic CRUD operations
    List<CommodityRecord> findByCommunityUnitId(Long communityUnitId);
    List<CommodityRecord> findByCommodityId(Long commodityId);
    List<CommodityRecord> findByCommunityUnitIdAndCommodityId(Long communityUnitId, Long commodityId);
    List<CommodityRecord> findByChp_Id(Long chpId);
    List<CommodityRecord> findByRecordDateBetween(LocalDateTime start, LocalDateTime end);
    List<CommodityRecord> findByStockOnHandLessThan(Integer threshold);
    
    // JPQL query for monthly consumption stats (PostgreSQL compatible)
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

    // PostgreSQL compatible native query for stock out stats
    @Query(value = """
        SELECT 
            cu.community_unit_name as communityUnitName,
            STRING_AGG(DISTINCT c.name, ', ') as commodityNames
        FROM commodity_records cr
        JOIN community_units cu ON cr.community_unit_id = cu.id
        JOIN commodities c ON cr.commodity_id = c.id
        WHERE cr.closing_balance <= 0 OR cr.closing_balance IS NULL
        GROUP BY cu.id, cu.community_unit_name
    """, nativeQuery = true)
    List<StockOutStat> getStockOutStats();

    // PostgreSQL compatible top consumption stats
    @Query(value = """
        SELECT 
            cu.community_unit_name as communityUnitName,
            COALESCE(SUM(cr.quantity_consumed), 0) as quantityConsumed
        FROM commodity_records cr
        JOIN community_units cu ON cr.community_unit_id = cu.id
        WHERE cr.record_date >= CURRENT_DATE - INTERVAL '30 days'
        GROUP BY cu.id, cu.community_unit_name
        ORDER BY SUM(cr.quantity_consumed) DESC
        LIMIT 3
        """, nativeQuery = true)
    List<ConsumptionProjection> getTopConsumptionStats();
    
    // Count community units with stock out
    @Query(value = """
        SELECT COUNT(DISTINCT cr.community_unit_id) 
        FROM commodity_records cr
        WHERE cr.closing_balance <= 0
        """, nativeQuery = true)
    Long countCommunityUnitsWithStockOut();
    
    // Count low stock items - Fixed to use the parameter
    @Query(value = """
        SELECT COUNT(*) 
        FROM commodity_records cr
        WHERE cr.closing_balance <= :threshold OR cr.closing_balance IS NULL
        """, nativeQuery = true)
    Long countLowStockItems(@Param("threshold") Integer threshold);
    
    // Get recent records for a community unit (JPQL - database agnostic)
    @Query("""
        SELECT cr FROM CommodityRecord cr
        WHERE cr.communityUnit.id = :communityUnitId
        ORDER BY cr.recordDate DESC
        """)
    List<CommodityRecord> findRecentRecordsByCommunityUnit(@Param("communityUnitId") Long communityUnitId);
    
    // Get consumption trends - PostgreSQL compatible
    @Query(value = """
        SELECT 
            DATE(cr.record_date) as recordDate,
            SUM(cr.quantity_consumed) as totalConsumed
        FROM commodity_records cr
        WHERE cr.record_date >= CURRENT_DATE - INTERVAL '30 days'
        GROUP BY DATE(cr.record_date)
        ORDER BY DATE(cr.record_date)
        """, nativeQuery = true)
    List<Object[]> getConsumptionTrends();
    
    // Find expired commodities (JPQL - database agnostic)
    @Query("""
        SELECT cr FROM CommodityRecord cr
        WHERE cr.quantityExpired > 0
        ORDER BY cr.recordDate DESC
        """)
    List<CommodityRecord> findRecordsWithExpiredItems();
    
    // Find damaged commodities (JPQL - database agnostic)
    @Query("""
        SELECT cr FROM CommodityRecord cr
        WHERE cr.quantityDamaged > 0
        ORDER BY cr.recordDate DESC
        """)
    List<CommodityRecord> findRecordsWithDamagedItems();

    // Fetch with locations (JPQL - database agnostic)
    @Query("SELECT cr FROM CommodityRecord cr " +
       "LEFT JOIN FETCH cr.communityUnit cu " +
       "LEFT JOIN FETCH cu.ward w " +
       "LEFT JOIN FETCH w.subCounty sc " +
       "LEFT JOIN FETCH sc.county")
    List<CommodityRecord> findAllWithLocations();

    // Get stock levels grouped by community unit (JPQL - database agnostic)
    @Query("""
        SELECT cr.communityUnit.id, SUM(cr.stockOnHand)
        FROM CommodityRecord cr
        GROUP BY cr.communityUnit.id
    """)
    List<Object[]> getStockLevelsGroupedByCommunityUnit();

    // Get total stock by community unit (JPQL - database agnostic)
    @Query("""
        SELECT COALESCE(SUM(cr.stockOnHand), 0)
        FROM CommodityRecord cr
        WHERE cr.communityUnit.id = :communityUnitId
    """)
    Integer getTotalStockByCommunityUnitId(@Param("communityUnitId") Long communityUnitId);

    List<CommodityRecord> findByChp_IdAndRecordDateBetween(Long chpId, LocalDateTime start, LocalDateTime end);


    // Find CHP record for specific CHP and commodity
    @Query("SELECT cr FROM CommodityRecord cr WHERE cr.chp.id = :chpId AND cr.commodity.id = :commodityId")
    CommodityRecord findByChpIdAndCommodityId(@Param("chpId") Long chpId, @Param("commodityId") Long commodityId);



@Query(value = """
    SELECT COALESCE(SUM(c.quantity_to_order), 0)
    FROM commodity_records c
    WHERE c.chp_id = :chpId
      AND c.commodity_id = :commodityId
      AND EXTRACT(MONTH FROM c.record_date) = EXTRACT(MONTH FROM CURRENT_DATE)
      AND EXTRACT(YEAR FROM c.record_date) = EXTRACT(YEAR FROM CURRENT_DATE)
""", nativeQuery = true)
Integer sumQuantityToOrderForCurrentMonth(@Param("chpId") Long chpId,
                                          @Param("commodityId") Long commodityId);

@Query(value = """
    SELECT *
    FROM commodity_records c
    WHERE c.chp_id = :chpId
      AND c.commodity_id = :commodityId
      AND EXTRACT(MONTH FROM c.record_date) = EXTRACT(MONTH FROM CURRENT_DATE)
      AND EXTRACT(YEAR FROM c.record_date) = EXTRACT(YEAR FROM CURRENT_DATE)
""", nativeQuery = true)
List<CommodityRecord> findAllForCurrentMonth(@Param("chpId") Long chpId,
                                             @Param("commodityId") Long commodityId);


}