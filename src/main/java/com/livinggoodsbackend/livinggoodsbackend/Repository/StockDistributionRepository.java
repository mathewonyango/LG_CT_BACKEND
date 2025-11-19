package com.livinggoodsbackend.livinggoodsbackend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.StockDistribution;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockDistributionRepository extends JpaRepository<StockDistribution, Long> {
    // Find distributions for a CHP, optionally filtered by commodity and date range
    @Query("SELECT d FROM StockDistribution d WHERE d.chpId = :chpId AND (:commodityId IS NULL OR d.commodityId = :commodityId) AND d.distributionDate BETWEEN :fromDate AND :toDate")
    Page<StockDistribution> findByChpIdAndCommodityId(@Param("chpId") Long chpId, @Param("commodityId") Long commodityId, @Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, Pageable pageable);

    // Find distributions for a CU (for CHA view)
    List<StockDistribution> findByCommunityUnitId(Long communityUnitId);



    @Query("SELECT d FROM StockDistribution d " +
       "WHERE d.chpId = :chpId " +
       "AND (:commodityId IS NULL OR d.commodityId = :commodityId) " +
       "AND d.distributionDate BETWEEN :fromDate AND :toDate " +
       "ORDER BY d.distributionDate DESC")
List<StockDistribution> findByChpId(Long chpId, Long commodityId, LocalDateTime fromDate, LocalDateTime toDate);



}
