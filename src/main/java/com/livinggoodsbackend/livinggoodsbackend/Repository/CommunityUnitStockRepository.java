package com.livinggoodsbackend.livinggoodsbackend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.livinggoodsbackend.livinggoodsbackend.Model.CommunityUnitStock;

import java.util.List;
@Repository
public interface CommunityUnitStockRepository extends JpaRepository<CommunityUnitStock, Long> {
    // Find stock summary for a Community Unit
    List<CommunityUnitStock> findByCommunityUnitId(Long communityUnitId);

    // Find stock for specific CU and commodity
@Query("SELECT s FROM CommunityUnitStock s " +
       "WHERE s.communityUnitId = :cuId AND s.commodityId = :commodityId " +
       "ORDER BY s.updatedAt DESC")
CommunityUnitStock findLatestByCommunityUnitIdAndCommodityId(
        @Param("cuId") Long cuId,
        @Param("commodityId") Long commodityId
);


    // Aggregate CHP needs for a CU and commodity
    @Query("SELECT SUM(cr.quantityToOrder) FROM CommodityRecord cr WHERE cr.communityUnit.id = :cuId AND cr.commodity.id = :commodityId")
    Integer sumChpQuantityToOrder(@Param("cuId") Long cuId, @Param("commodityId") Long commodityId);
}