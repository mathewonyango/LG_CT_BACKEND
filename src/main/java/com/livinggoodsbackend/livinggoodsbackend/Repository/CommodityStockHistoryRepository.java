package com.livinggoodsbackend.livinggoodsbackend.Repository;

import java.time.LocalDateTime;
import java.util.List;
// import com.livinggoodsbackend.livinggoodsbackend.Repository.ChangeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.livinggoodsbackend.livinggoodsbackend.Model.CommodityStockHistory;
import com.livinggoodsbackend.livinggoodsbackend.Model.User;

@Repository
public interface CommodityStockHistoryRepository extends JpaRepository<CommodityStockHistory, Long> {
    List<CommodityStockHistory> findByCommunityUnitId(Long communityUnitId);
    List<CommodityStockHistory> findByCommodityId(Long commodityId);
    //  List<CommodityStockHistory> findByChangeType(ChangeType changeType);
    List<CommodityStockHistory> findByRecordedBy(User recordedBy);
    List<CommodityStockHistory> findByRecordDateBetween(LocalDateTime start, LocalDateTime end);
}
